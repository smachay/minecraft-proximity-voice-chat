package com.pvchat.proximityvoicechat.plugin.socket;


import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class responsoble for generating self-signed certificate which is used
 */
public class SSLCertUtils {

    /**
     * Generates new RSA key pair with size 2048
     *
     * @return new RSA {@link KeyPair}
     */
    public static KeyPair generateKeyPair() {
        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048, new SecureRandom());
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * @param keyPair keypair
     * @param names   information about certificate owner
     * @return self-signed {@link X509Certificate} from given keypair.
     * @throws OperatorCreationException
     * @throws CertificateException
     * @throws IOException
     */
    public static X509Certificate selfSign(KeyPair keyPair, GeneralNames names) throws OperatorCreationException, CertificateException, IOException {
        Provider bcProvider = new BouncyCastleProvider();
        Security.addProvider(bcProvider);

        long now = System.currentTimeMillis();
        Date startDate = new Date(now);

        X500Name dnName = new X500Name("dc=localhost");
        BigInteger certSerialNumber = new BigInteger(Long.toString(now));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, 1);

        Date endDate = calendar.getTime();

        String signatureAlgorithm = "SHA256WithRSA";

        ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).build(keyPair.getPrivate());

        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(dnName, certSerialNumber, startDate, endDate, dnName, keyPair.getPublic());
        certBuilder.addExtension(Extension.subjectAlternativeName, false, names);

        BasicConstraints basicConstraints = new BasicConstraints(true);

        certBuilder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, basicConstraints);

        return new JcaX509CertificateConverter().setProvider(bcProvider).getCertificate(certBuilder.build(contentSigner));
    }

    /**
     * Stores given private key ({@link PrivateKey}) in file.
     *
     * @param key      key to save.
     * @param fileName filename.
     * @throws IOException
     */
    public static void savePrivateKey(PrivateKey key, String fileName) throws IOException {
        var encoder = Base64.getEncoder();
        try (var privKeyOut = new FileOutputStream(fileName)) {
            var privKey = encoder.encode(key.getEncoded());
            privKeyOut.write("-----BEGIN RSA PRIVATE KEY-----\n".getBytes());
            privKeyOut.write(privKey);
            privKeyOut.write("\n-----END RSA PRIVATE KEY-----\n".getBytes());
        }
    }

    /**
     * Returns default {@link SSLContext}
     *
     * @return default {@link SSLContext}
     */
    public static SSLContext getDefaultSSLContext() {
        KeyStore ks = null;
        SSLContext sslContext;
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(Files.newInputStream(Paths.get("keystore")), "".toCharArray());
            var kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, "".toCharArray());
            var tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException |
                CertificateException | IOException e) {
            throw new IllegalArgumentException();
        }
        return sslContext;
    }

    /**
     * Stores given ({@link Certificate}) in file.
     *
     * @param certificate certificate to save.
     * @param fileName    filename.
     * @throws IOException
     */
    public static void saveCertificate(Certificate certificate, String fileName) throws IOException, CertificateEncodingException {
        var encoder = Base64.getEncoder();
        try (var certOut = new FileOutputStream(fileName)) {
            var certBytes = certificate.getEncoded();
            certBytes = encoder.encode(certBytes);
            certOut.write("-----BEGIN CERTIFICATE-----\n".getBytes());
            certOut.write(certBytes);
            certOut.write("\n-----END CERTIFICATE-----\n".getBytes());
        }
    }

    /**
     * Creates new keystore, saves newly created certificate to it and stores the keystore in the file named keystore.
     *
     * @param names certificate owner info.
     * @throws CertificateException
     * @throws IOException
     * @throws OperatorCreationException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     */
    public static void generateKeyStore(GeneralNames names) throws CertificateException, IOException, OperatorCreationException, KeyStoreException, NoSuchAlgorithmException {
        var keyPair = generateKeyPair();
        var cert = selfSign(keyPair, names);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, "".toCharArray());
        ks.setKeyEntry("localhost", keyPair.getPrivate(), "".toCharArray(), new java.security.cert.Certificate[]{cert});
        ks.store(new FileOutputStream("keystore"), "".toCharArray());
    }

    /**
     * Verifies if the file called "keystore" exists. If file doesn't exist, it creates new keystore, generates new keypair, creates new self-signed certificate out of the generated keypair and stores it in the keystore.
     *
     * @param logger logger
     * @param names  certificate owner info.
     */
    public static void verifyKeyStorePresent(Logger logger, GeneralNames names) {
        if (FileSystems.getDefault().getPath("keystore").toFile().exists()) {
            logger.info("Key store detected, using existing one.");
            return;
        }
        logger.info("Key store was not detected, creating a new one.");
        logger.info("################################################################");
        logger.info("New key store and certificate will now be generated.");
        logger.info("Please make sure you configured server ip/dns names properly.");
        logger.info("################################################################");
        try {
            generateKeyStore(names);
            logger.info("Key store and certificate generated successfully. You can now install certificate in your system.");
        } catch (CertificateException | IOException | OperatorCreationException | KeyStoreException |
                NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, "Could not generate key store, make sure config is valid. It is not possible to start plugin.");
            throw new IllegalArgumentException("Could not generate key store.");
        }
    }

}
