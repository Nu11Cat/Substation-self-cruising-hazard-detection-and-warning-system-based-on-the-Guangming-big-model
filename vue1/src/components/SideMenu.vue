<template>
  <el-menu
    :default-active="activeIndex"
    class="main-menu"
    @select="handleSelect"
    background-color="#304156"
    text-color="#fff"
    active-text-color="#409EFF">
    
    <el-menu-item index="/image-detection" class="menu-item">
      <i class="el-icon-picture"></i>
      <span slot="title">图片检测</span>
    </el-menu-item>

    <el-menu-item index="/image-list" class="menu-item">
      <i class="el-icon-document"></i>
      <span slot="title">图片列表</span>
    </el-menu-item>

    <el-menu-item index="/statistics" class="menu-item">
      <i class="el-icon-data-line"></i>
      <span slot="title">统计分析</span>
    </el-menu-item>

    <el-menu-item index="/model-management" class="menu-item">
      <i class="el-icon-cpu"></i>
      <span slot="title">模型管理</span>
    </el-menu-item>

    <el-menu-item index="/settings" class="menu-item">
      <i class="el-icon-setting"></i>
      <span slot="title">系统设置</span>
    </el-menu-item>
  </el-menu>
</template>

<script>
export default {
  name: 'SideMenu',
  data() {
    return {
      activeIndex: this.$route.path
    }
  },
  methods: {
    handleSelect(key) {
      this.$router.push(key)
    }
  },
  watch: {
    '$route.path': {
      handler(newPath) {
        this.activeIndex = newPath
      },
      immediate: true
    }
  }
}
</script>

<style scoped>
.main-menu {
  border: none;
  padding: 10px 0;
}

.menu-item {
  margin: 4px 12px;
  height: 56px;
  line-height: 56px;
  padding: 0 20px !important;
  transition: all 0.3s ease !important;
  border-radius: 8px;
  position: relative;
  overflow: hidden;
}

.menu-item:hover {
  background-color: rgba(64, 158, 255, 0.15) !important;
  transform: translateX(5px);
}

.menu-item i {
  font-size: 18px;
  margin-right: 10px;
  transition: transform 0.3s ease;
}

.menu-item:hover i {
  transform: scale(1.1);
}

.menu-item.is-active {
  background: linear-gradient(90deg, rgba(64, 158, 255, 0.25), rgba(64, 158, 255, 0.05)) !important;
  border-left: 3px solid #409EFF;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.15);
}

.menu-item.is-active::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, rgba(64, 158, 255, 0.1), transparent);
  animation: shine 2s infinite;
}

@keyframes shine {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}
</style> 