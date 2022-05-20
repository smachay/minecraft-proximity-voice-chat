package com.pvchat.proximityvoicechat.plugin.socket;


import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.encoders.Base64Encoder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class SSLCertManager {
    public SSLCertManager() throws RuntimeException {
    }

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

    public static X509Certificate selfSign(KeyPair keyPair, String subjectDN) throws OperatorCreationException, CertificateException, IOException {
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
        GeneralNames namesList = new GeneralNames(new GeneralName[]{new GeneralName(GeneralName.dNSName, "localhost")});
        certBuilder.addExtension(Extension.subjectAlternativeName, false, namesList);

        BasicConstraints basicConstraints = new BasicConstraints(true);

        certBuilder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, basicConstraints);

        return new JcaX509CertificateConverter().setProvider(bcProvider).getCertificate(certBuilder.build(contentSigner));
    }

    public static void savePrivateKey(PrivateKey key, String fileName) throws IOException {
        var encoder = Base64.getEncoder();
        try (var privKeyOut = new FileOutputStream(fileName)) {
            var privKey = encoder.encode(key.getEncoded());
            privKeyOut.write("-----BEGIN RSA PRIVATE KEY-----\n".getBytes());
            privKeyOut.write(privKey);
            privKeyOut.write("\n-----END RSA PRIVATE KEY-----\n".getBytes());
        }
    }

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

    public static void generateKeyStore() throws CertificateException, IOException, OperatorCreationException, KeyStoreException, NoSuchAlgorithmException {
        var keyPair = generateKeyPair();
        var cert = selfSign(keyPair, "dc=127.0.0.1");
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, "".toCharArray());
        ks.setKeyEntry("localhost", keyPair.getPrivate(), "".toCharArray(), new java.security.cert.Certificate[]{cert});
        ks.store(new FileOutputStream("keystore"), "".toCharArray());
    }

}
