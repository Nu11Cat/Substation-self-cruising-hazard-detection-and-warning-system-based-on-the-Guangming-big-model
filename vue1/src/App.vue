<template>
  <div id="app">
    <el-container>
      <el-header height="60px" class="main-header">
        <div class="header-content">
          <div class="header-left">
            <img src="@/assets/logo.png" alt="Logo" class="logo-img">
            <div class="header-title">基于YOLO的变电站隐患检测系统</div>
          </div>
          <div class="header-right">
            <el-dropdown trigger="click">
              <span class="user-profile">
                <i class="el-icon-user-solid"></i>
                admin
                <i class="el-icon-caret-bottom"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item>
                  <i class="el-icon-setting"></i>
                  账号设置
                </el-dropdown-item>
                <el-dropdown-item divided>
                  <i class="el-icon-switch-button"></i>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      <el-container>
        <el-aside width="220px" class="main-aside">
          <div class="menu-header">
            <i class="el-icon-menu"></i>
            功能导航
          </div>
          <side-menu></side-menu>
        </el-aside>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import SideMenu from './components/SideMenu.vue'

export default {
  name: 'App',
  components: {
    SideMenu
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100vh;
  color: #2c3e50;
}

.el-container {
  height: 100%;
  background-color: #f0f2f5;
}

/* 顶部导航样式 */
.main-header {
  background: linear-gradient(90deg, #304156, #485e7f);
  padding: 0;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 100;
  border-bottom-left-radius: 12px;
  border-bottom-right-radius: 12px;
}

.header-content {
  height: 100%;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logo-img {
  height: 40px;
  width: auto;
  transition: transform 0.3s ease;
}

.logo-img:hover {
  transform: scale(1.05);
}

.header-title {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  background: linear-gradient(90deg, #fff, #e6e6e6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-profile {
  color: #fff;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 20px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(5px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-profile:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 左侧导航样式 */
.main-aside {
  background-color: #304156;
  box-shadow: 3px 0 15px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 99;
  transition: all 0.3s ease;
  border-top-right-radius: 12px;
  border-bottom-right-radius: 12px;
  margin: 8px 0 8px 0;
  height: calc(100% - 16px);
}

.menu-header {
  height: 50px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  background: rgba(255, 255, 255, 0.05);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  border-top-right-radius: 12px;
  backdrop-filter: blur(5px);
}

.menu-header i {
  margin-right: 10px;
  font-size: 18px;
}

.main-menu {
  border: none;
  padding-top: 10px;
}

.menu-item {
  margin: 4px 12px;
  height: 56px;
  line-height: 56px;
  padding: 0 20px !important;
  transition: all 0.3s ease !important;
  border-radius: 8px;
}

.menu-item:hover {
  background-color: rgba(64, 158, 255, 0.15) !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.menu-item i {
  font-size: 18px;
  margin-right: 10px;
  transition: transform 0.3s ease;
}

.menu-item:hover i {
  transform: translateX(3px);
}

.menu-item.is-active {
  background: linear-gradient(90deg, rgba(64, 158, 255, 0.25), rgba(64, 158, 255, 0.05)) !important;
  border-left: 3px solid #409EFF;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.15);
}

.el-main {
  background-color: #f5f7fa;
  padding: 20px;
  height: calc(100vh - 60px);
  overflow-y: auto;
  border-radius: 12px;
  margin: 0 8px 8px 0;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 4px;
}

::-webkit-scrollbar-track {
  background: #f5f7fa;
}

/* 下拉菜单样式 */
.el-dropdown-menu {
  padding: 5px 0;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15) !important;
  border: none !important;
}

.el-dropdown-menu__item {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  border-radius: 4px;
  margin: 0 5px;
}

.el-dropdown-menu__item i {
  margin-right: 8px;
  font-size: 16px;
}

.el-dropdown-menu__item:hover {
  background-color: #ecf5ff;
  color: #409EFF;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

/* 响应式调整 */
@media screen and (max-width: 1200px) {
  .main-aside {
    width: 200px !important;
  }

  .header-title {
    font-size: 18px;
  }
}

@media screen and (max-width: 768px) {
  .main-aside {
    width: 180px !important;
  }

  .header-title {
    font-size: 16px;
  }

  .logo-img {
    height: 35px;
  }
}
</style>
