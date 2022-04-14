/**
 * @name MinecraftVoiceChat
 * @author Stefan Machay, Adam Barankevych
 * @description Describe the basic functions. Maybe a support server link.
 * @source https://github.com/smachay/minecraft-proximity-voice-chat
 /
**/

"use strict";

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
  const users = JSON.parse(userVolumes);

  for (const { userId, volume } of users) {
    setVolume(userId, volume);
  }
};

module.exports = class MinecraftVoiceChat {
  load() {}

  start() {}
  stop() {}
};

const socket = new WebSocket("url");

socket.onopen = function (e) {
  socket.send("Connected");
};

socket.onmessage = function (event) {
  updateUserVolumes(event.data);
};
