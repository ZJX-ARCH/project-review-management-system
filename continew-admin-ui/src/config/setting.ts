export const defaultSettings: App.AppSettings = {
  theme: 'light',
  themeColor: '#409EFF',
  tab: true,
  tabMode: 'rounded',
  animate: false,
  animateMode: 'zoom-fade',
  menuCollapse: false,
  menuAccordion: true,
  menuDark: false,
  copyrightDisplay: true,
  layout: 'left',
  isOpenWatermark: false,
  enableColorWeaknessMode: false,
  enableMourningMode: false,
}
// 根据环境返回配置
export const getSettings = (): App.AppSettings => {
  return defaultSettings
}
