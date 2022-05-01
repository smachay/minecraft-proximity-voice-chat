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
const AudioConvert = byProps("perceptualToAmplitude");

const setVolume = (userId, value) => {
  SettingsActions.setLocalVolume(
    userId,
    AudioConvert.perceptualToAmplitude(value),
    "default"
  );
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

  if (currId === channelPVC && socket === null) {
    //IP: ws://localhost:8080?discordID=0
    socket = new WebSocket(`ws://localhost:8080?discordID=${userId}`);
    socket.onopen = function (e) {
      socket.send("Connected");
    };
    socket.onmessage = function (event) {
      updateUserVolumes(event.data);
    };
  } else {
    if (socket != null) {
      socket = null;
    }
  }
};

module.exports = class MinecraftVoiceChat {
  load() {
    socket = null;
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
