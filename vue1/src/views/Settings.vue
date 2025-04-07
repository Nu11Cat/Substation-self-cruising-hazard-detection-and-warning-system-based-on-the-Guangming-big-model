<template>
  <div class="settings-container">
    <el-card class="settings-card">
      <h2 class="settings-header">系统设置</h2>
      <el-form :model="settings" label-width="140px" size="small">
        <!-- 图片处理设置 -->
        <el-divider content-position="left">图片处理设置</el-divider>
        <el-form-item label="检测阈值">
          <el-slider v-model="settings.threshold" :step="0.1" :min="0" :max="1"></el-slider>
          <div class="setting-item-desc">设置图片检测的灵敏度，值越大越严格</div>
        </el-form-item>
        <el-form-item label="处理模式">
          <el-select v-model="settings.processMode" placeholder="请选择处理模式">
            <el-option label="快速模式" value="fast"></el-option>
            <el-option label="标准模式" value="standard"></el-option>
            <el-option label="精确模式" value="accurate"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="图片大小限制">
          <el-input-number v-model="settings.maxImageSize" :min="1" :max="20" :step="1"></el-input-number>
          <span style="margin-left: 8px;">MB</span>
          <div class="setting-item-desc">单个图片文件的最大上传限制</div>
        </el-form-item>
        <el-form-item label="支持的格式">
          <el-checkbox-group v-model="settings.allowedFormats">
            <el-checkbox label="jpg">JPG</el-checkbox>
            <el-checkbox label="png">PNG</el-checkbox>
            <el-checkbox label="jpeg">JPEG</el-checkbox>
            <el-checkbox label="gif">GIF</el-checkbox>
            <el-checkbox label="webp">WEBP</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <!-- 系统设置 -->
        <el-divider content-position="left">系统设置</el-divider>
        <el-form-item label="主题">
          <el-radio-group v-model="settings.theme">
            <el-radio label="light">浅色</el-radio>
            <el-radio label="dark">深色</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="语言">
          <el-select v-model="settings.language">
            <el-option label="简体中文" value="zh-CN"></el-option>
            <el-option label="English" value="en-US"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="自动保存">
          <el-switch v-model="settings.autoSave"></el-switch>
          <div class="setting-item-desc">开启后系统将自动保存您的设置</div>
        </el-form-item>
        <el-form-item label="数据备份">
          <el-switch v-model="settings.autoBackup"></el-switch>
          <div class="setting-item-desc">开启后系统将定期备份数据</div>
        </el-form-item>
        <el-form-item label="备份频率">
          <el-select v-model="settings.backupFrequency" :disabled="!settings.autoBackup">
            <el-option label="每天" value="daily"></el-option>
            <el-option label="每周" value="weekly"></el-option>
            <el-option label="每月" value="monthly"></el-option>
          </el-select>
        </el-form-item>

        <!-- 系统性能设置 -->
        <el-divider content-position="left">系统性能</el-divider>
        <el-form-item label="并发处理数">
          <el-input-number v-model="settings.concurrency" :min="1" :max="10"></el-input-number>
          <div class="setting-item-desc">同时处理的最大图片数量</div>
        </el-form-item>
        <el-form-item label="缓存策略">
          <el-select v-model="settings.cacheStrategy" placeholder="请选择缓存策略">
            <el-option label="不使用缓存" value="none"></el-option>
            <el-option label="基础缓存" value="basic"></el-option>
            <el-option label="增强缓存" value="enhanced"></el-option>
          </el-select>
          <div class="setting-item-desc">设置系统缓存策略，影响加载速度和内存占用</div>
        </el-form-item>

        <!-- 通知设置 -->
        <el-divider content-position="left">通知设置</el-divider>
        <el-form-item label="处理完成通知">
          <el-switch v-model="settings.processNotification"></el-switch>
          <div class="setting-item-desc">图片处理完成后是否显示通知</div>
        </el-form-item>
        <el-form-item label="错误提醒">
          <el-switch v-model="settings.errorNotification"></el-switch>
          <div class="setting-item-desc">系统发生错误时是否显示通知</div>
        </el-form-item>
        <el-form-item label="通知声音">
          <el-switch v-model="settings.notificationSound"></el-switch>
        </el-form-item>

        <!-- 日志设置 -->
        <el-divider content-position="left">日志设置</el-divider>
        <el-form-item label="日志保留时间">
          <el-select v-model="settings.logRetention" placeholder="请选择保留时间">
            <el-option label="7天" value="7"></el-option>
            <el-option label="30天" value="30"></el-option>
            <el-option label="90天" value="90"></el-option>
            <el-option label="180天" value="180"></el-option>
            <el-option label="永久" value="-1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日志等级">
          <el-select v-model="settings.logLevel" placeholder="请选择日志等级">
            <el-option label="调试" value="debug"></el-option>
            <el-option label="信息" value="info"></el-option>
            <el-option label="警告" value="warning"></el-option>
            <el-option label="错误" value="error"></el-option>
          </el-select>
        </el-form-item>

        <!-- 高级设置 -->
        <el-divider content-position="left">高级设置</el-divider>
        <el-form-item label="开发者模式">
          <el-switch v-model="settings.devMode"></el-switch>
          <div class="setting-item-desc">启用后显示更多调试信息</div>
        </el-form-item>
        <el-form-item label="实验性功能">
          <el-switch v-model="settings.experimental"></el-switch>
          <div class="setting-item-desc">启用实验性功能（可能不稳定）</div>
        </el-form-item>
        <el-form-item label="API地址">
          <el-input v-model="settings.apiUrl" placeholder="请输入API地址"></el-input>
        </el-form-item>

        <!-- 按钮操作区 -->
        <div class="action-bar">
          <el-button type="primary" @click="handleSave">保存设置</el-button>
          <el-button @click="handleReset">恢复默认</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'SystemSettings',
  data() {
    return {
      settings: {
        threshold: 0.5,
        processMode: 'standard',
        maxImageSize: 5,
        allowedFormats: ['jpg', 'png', 'jpeg'],
        theme: 'light',
        language: 'zh-CN',
        autoSave: true,
        autoBackup: false,
        backupFrequency: 'weekly',
        concurrency: 3,
        cacheStrategy: 'basic',
        processNotification: true,
        errorNotification: true,
        notificationSound: true,
        logRetention: '30',
        logLevel: 'info',
        devMode: false,
        experimental: false,
        apiUrl: 'http://localhost:3000/api'
      },
      modelFile: null,
      currentModel: 'best.pt',
      modelList: []
    }
  },
  created() {
    // 加载当前模型和模型列表
    this.loadModelData()
  },
  methods: {
    loadModelData() {
      // 获取当前模型
      axios.get('http://localhost:8080/model/current')
        .then(response => {
          if (response.data.code === 1 && response.data.data) {
            this.currentModel = response.data.data.currentModel
          }
        })
        .catch(error => {
          console.error('获取当前模型失败:', error)
        })
      
      // 获取模型列表
      axios.get('http://localhost:8080/model/list')
        .then(response => {
          if (response.data.code === 1 && response.data.data) {
            this.modelList = response.data.data.map(name => ({ name }))
          }
        })
        .catch(error => {
          console.error('获取模型列表失败:', error)
        })
    },
    handleSave() {
      this.$message.success('设置保存成功')
    },
    handleReset() {
      this.$confirm('确定要恢复默认设置吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.settings = {
          threshold: 0.5,
          processMode: 'standard',
          maxImageSize: 5,
          allowedFormats: ['jpg', 'png', 'jpeg'],
          theme: 'light',
          language: 'zh-CN',
          autoSave: true,
          autoBackup: false,
          backupFrequency: 'weekly',
          concurrency: 3,
          cacheStrategy: 'basic',
          processNotification: true,
          errorNotification: true,
          notificationSound: true,
          logRetention: '30',
          logLevel: 'info',
          devMode: false,
          experimental: false,
          apiUrl: 'http://localhost:3000/api'
        }
        this.$message.success('已恢复默认设置')
      }).catch(() => {})
    },
    handleModelSuccess(response, file) {
      // eslint-disable-next-line no-unused-vars
      this.modelFile = file
      this.$message.success('模型上传成功')
      // 刷新模型列表
      this.loadModelData()
    },
    // eslint-disable-next-line no-unused-vars
    handleModelError(error, file) {
      this.$message.error('模型上传失败: ' + (error.message || '未知错误'))
    },
    reloadModel() {
      axios.post('http://localhost:5000/reload_model')
        // eslint-disable-next-line no-unused-vars
        .then(response => {
          this.$message.success('模型重新加载成功')
        })
        .catch(error => {
          this.$message.error('模型重新加载失败: ' + (error.message || '未知错误'))
        })
    },
    setCurrentModel(modelName) {
      axios.post('http://localhost:8080/model/setCurrent', null, {
        params: { modelName }
      })
        .then(response => {
          if (response.data.code === 1) {
            this.currentModel = modelName
            this.$message.success('当前模型已更新')
          } else {
            this.$message.error(response.data.msg || '设置当前模型失败')
          }
        })
        .catch(error => {
          this.$message.error('设置当前模型失败: ' + (error.message || '未知错误'))
        })
    },
    deleteModel(modelName) {
      this.$confirm(`确定要删除模型 ${modelName} 吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        axios.delete(`http://localhost:8080/model/${modelName}`)
          .then(response => {
            if (response.data.code === 1) {
              this.$message.success('模型删除成功')
              // 刷新模型列表
              this.loadModelData()
            } else {
              this.$message.error(response.data.msg || '删除模型失败')
            }
          })
          .catch(error => {
            this.$message.error('删除模型失败: ' + (error.message || '未知错误'))
          })
      }).catch(() => {
        // 用户取消删除
      })
    }
  }
}
</script>

<style scoped>
.settings-container {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #f0f2f5 100%);
  min-height: calc(100vh - 100px);
  margin: -20px;
  border-radius: 12px;
}

.settings-card {
  margin-bottom: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08) !important;
  border: none;
}

.settings-header {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 24px;
  position: relative;
  padding-left: 14px;
}

.settings-header::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background: linear-gradient(to bottom, #409EFF, #53a8ff);
  border-radius: 2px;
}

.el-divider {
  margin: 32px 0 24px;
}

.el-divider__text {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  background: #fff;
}

.setting-item {
  margin-bottom: 24px;
}

.setting-item-title {
  font-size: 15px;
  color: #333;
  margin-bottom: 8px;
}

.setting-item-desc {
  color: #909399;
  font-size: 12px;
  margin-top: 4px;
}

.el-form-item {
  margin-bottom: 28px;
}

.el-form :deep(.el-form-item__label) {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.el-form :deep(.el-input__inner) {
  font-size: 14px;
}

.el-form :deep(.el-radio-group) {
  margin-top: 8px;
}

.el-form :deep(.el-checkbox-group) {
  margin-top: 8px;
}

.action-bar {
  display: flex;
  justify-content: flex-start;
  margin-top: 30px;
}

.action-bar .el-button {
  margin-right: 12px;
}

.model-uploader {
  display: flex;
  align-items: center;
}

.model-uploader .el-upload {
  display: inline-block;
  margin-right: 10px;
}

.model-list {
  margin-top: 10px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}
</style> 