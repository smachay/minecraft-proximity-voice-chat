/**
 * @name MinecraftVoiceChat
 * @author Stefan Machay, Adam Barankevych
 * @description Describe the basic functions. Maybe a support server link.
 * @source https://github.com/smachay/minecraft-proximity-voice-chat
 */

"use strict";

const userId = BdApi.findModuleByProps("getCurrentUser").getCurrentUser().id;

let channelPVC = "949028178599510061";
let socket;
let previousVolumes;

const join = (filters) => {
  const apply = filters.filter((filter) => filter instanceof Function);

  return (exports) => apply.every((filter) => filter(exports));
};

const byProps$1 = (props) => {
  return (target) =>
    target instanceof Object && props.every((prop) => prop in target);
};

const raw = {
  single: (filter) => BdApi.findModule(filter),
  all: (filter) => BdApi.findAllModules(filter),
};

const find = (...filters) => raw.single(join(filters));
const byProps = (...props) => find(byProps$1(props));

const SettingsActions = byProps("setLocalVolume");
const getSettingsActions = byProps("getLocalVolume");
const AudioConvert = byProps("perceptualToAmplitude");

const setVolume = (userId, value) => {
  setPreviousVolumes(userId);
  SettingsActions.setLocalVolume(
    userId,
    AudioConvert.perceptualToAmplitude(value),
    "default"
  );
};

const getVolume = (userId) => {
  const volume = getSettingsActions.getLocalVolume(userId, "default");
  return AudioConvert.amplitudeToPerceptual(volume);
};

//checks if user's initial volume value was saved
const setPreviousVolumes = (userId) => {
  if (previousVolumes === null) {
    previousVolumes = [];
    previousVolumes.push({ user: userId, volume: getVolume(userId) });
  } else if (!previousVolumes.find(({ user }) => user === userId)) {
    previousVolumes.push({ user: userId, volume: getVolume(userId) });
  }
};

//restores user local volumes from before they entered the channel
const restorePreviousVolumes = () => {
  if (previousVolumes !== null) {
    for (const { user, volume } of previousVolumes) {
      setTimeout(() => {
        setVolume(user, volume);
      }, 1000);
    }
  }
};

const updateUserVolumes = (userVolumes) => {
  const users = JSON.parse(userVolumes).volumeData;

  for (const { player1, player2, volume } of users) {
    if (player1 != userId) setVolume(player1, volume);
    else setVolume(player2, volume);
  }
};

//checks if current voice channel equals proximity voice channel
const checkChannel = () => {
  const currId = BdApi.findModuleByProps(
    "getLastSelectedChannelId"
  ).getVoiceChannelId();

  if (currId === channelPVC) {
    socket = new WebSocket(`ws://localhost:8080?discordID=${userId}`);
    socket.onopen = function (e) {
      socket.send("Connected");
    };
    socket.onmessage = function (event) {
      updateUserVolumes(event.data);
    };
  } else if (socket !== null) {
    socket.close(1000);
    socket = null;
    restorePreviousVolumes();
    previousVolumes = null;
  }
};

module.exports = class MinecraftVoiceChat {
  load() {
    socket = null;
    previousVolumes = null;
    checkChannel();
  }

  start() {
    BdApi.findModuleByProps("getLastSelectedChannelId").addChangeListener(
      () => {
        checkChannel();
      }
    );
  }
  stop() {}
};
