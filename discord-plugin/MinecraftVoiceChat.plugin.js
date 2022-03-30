/**
 * @name MinecraftVoiceChat
 * @author Stefan Machay, Adam Barankevych
 * @description Describe the basic functions. Maybe a support server link.
 * @source https://github.com/smachay/minecraft-proximity-voice-chat
 */

"use strict";

const join = (filters) => {
  const apply = filters.filter((filter) => filter instanceof Function);

  return (exports) => apply.every((filter) => filter(exports));
};

const byName$1 = (name) => {
  return (target) =>
    target instanceof Object && Object.values(target).some(byOwnName(name));
};
const byOwnName = (name) => {
  return (target) =>
    target?.displayName === name || target?.constructor?.displayName === name;
};
const byProps$1 = (props) => {
  return (target) =>
    target instanceof Object && props.every((prop) => prop in target);
};
const byProtos = (protos) => {
  return (target) =>
    target instanceof Object &&
    target.prototype instanceof Object &&
    protos.every((proto) => proto in target.prototype);
};
const bySource = (contents) => {
  return (target) =>
    target instanceof Function &&
    contents.every((content) => target.toString().includes(content));
};

const raw = {
  single: (filter) => BdApi.findModule(filter),
  all: (filter) => BdApi.findAllModules(filter),
};
const resolveExports = (target, filter) => {
  if (target) {
    if (typeof filter === "string") {
      return target[filter];
    } else if (filter instanceof Function) {
      return filter(target)
        ? target
        : Object.values(target).find((entry) => filter(entry));
    }
  }
  return target;
};
const find = (...filters) => raw.single(join(filters));

const byName = (name) => resolveExports(find(byName$1(name)), byOwnName(name));
const byProps = (...props) => find(byProps$1(props));

const EventEmitter = () => byProps("subscribe", "emit");
const React$1 = () => byProps("createElement", "Component", "Fragment");
const ReactDOM$1 = () => byProps("render", "findDOMNode", "createPortal");
const classNames$1 = () =>
  find(
    (exports) =>
      exports instanceof Object &&
      exports.default === exports &&
      Object.keys(exports).length === 1
  );
const lodash$1 = () => byProps("cloneDeep", "flattenDeep");
const semver = () => byProps("valid", "satifies");
const moment = () => byProps("utc", "months");
const SimpleMarkdown = () => byProps("parseBlock", "parseInline");
const hljs = () => byProps("highlight", "highlightBlock");
const Raven = () => byProps("captureBreadcrumb");
const joi = () => byProps("assert", "validate", "object");

const npm = {
  __proto__: null,
  EventEmitter: EventEmitter,
  React: React$1,
  ReactDOM: ReactDOM$1,
  classNames: classNames$1,
  lodash: lodash$1,
  semver: semver,
  moment: moment,
  SimpleMarkdown: SimpleMarkdown,
  hljs: hljs,
  Raven: Raven,
  joi: joi,
};

const Flux$1 = () => byProps("Store", "useStateFromStores");
const Events = () => byProps("dirtyDispatch");

const flux = {
  __proto__: null,
  Flux: Flux$1,
  Events: Events,
};

const Constants = () => byProps("Permissions", "RelationshipTypes");
const i18n = () => byProps("languages", "getLocale");
const Channels = () => byProps("getChannel", "hasChannel");
const SelectedChannel = () => byProps("getChannelId", "getVoiceChannelId");
const Users = () => byProps("getUser", "getCurrentUser");
const Members = () => byProps("getMember", "isMember");
const ContextMenuActions = () => byProps("openContextMenuLazy");
const ModalActions = () => byProps("openModalLazy");
const Flex$1 = () => byName("Flex");
const Button$1 = () => byProps("Link", "Hovers");
const Text = () => byName("Text");
const Links = () => byProps("Link", "NavLink");
const Switch = () => byName("Switch");
const SwitchItem = () => byName("SwitchItem");
const RadioGroup = () => byName("RadioGroup");
const Slider = () => byName("Slider");
const TextInput = () => byName("TextInput");
const Menu = () => byProps("MenuGroup", "MenuItem", "MenuSeparator");
const Form$1 = () => byProps("FormItem", "FormSection", "FormDivider");
const margins$1 = () => byProps("marginLarge");

const discord = {
  __proto__: null,
  Constants: Constants,
  i18n: i18n,
  Channels: Channels,
  SelectedChannel: SelectedChannel,
  Users: Users,
  Members: Members,
  ContextMenuActions: ContextMenuActions,
  ModalActions: ModalActions,
  Flex: Flex$1,
  Button: Button$1,
  Text: Text,
  Links: Links,
  Switch: Switch,
  SwitchItem: SwitchItem,
  RadioGroup: RadioGroup,
  Slider: Slider,
  TextInput: TextInput,
  Menu: Menu,
  Form: Form$1,
  margins: margins$1,
};

const createProxy = (entries) => {
  const result = {};
  for (const [key, value] of Object.entries(entries)) {
    Object.defineProperty(result, key, {
      enumerable: true,
      configurable: true,
      get() {
        delete this[key];
        this[key] = value();
        return this[key];
      },
    });
  }
  return result;
};
const Modules = createProxy({
  ...npm,
  ...flux,
  ...discord,
});

const SettingsActions = byProps("setLocalVolume");
const AudioConvert = byProps("perceptualToAmplitude");

const setVolume = (userId, value) => {
  SettingsActions.setLocalVolume(
    userId,
    AudioConvert.perceptualToAmplitude(value),
    "default"
  );
};

function updateUserVolumes(userVolumes) {
  for (const { userId, volume } of userVolumes) {
    setVolume(userId, volume);
  }
}

module.exports = class MinecraftVoiceChat {
  load() {}

  async start() {
    setInterval(() => {
      updateUserVolumes([
        {
          userId: 226681495127982080,
          volume: Math.random() * 50 + 50,
        },
      ]);
    }, 1000);
  }
  stop() {}

  observer(changes) {}
};
