<template>
  <div class="model-management">
    <el-card class="model-card">
      <div slot="header" class="card-header">
        <span>模型管理</span>
      </div>
      
      <!-- 当前模型信息 -->
      <div class="current-model-section">
        <div class="current-model-label">当前使用的模型</div>
        <div class="current-model-value">{{ currentModel }}</div>
      </div>

      <!-- 模型上传 -->
      <div class="upload-section">
        <h3>上传新模型</h3>
        <el-upload
          class="model-uploader"
          action="http://localhost:8080/model/upload"
          :on-success="handleModelSuccess"
          :on-error="handleModelError"
          :show-file-list="false">
          <el-button type="primary">上传新模型文件</el-button>
          <div class="upload-tip">支持.pt格式的YOLO模型文件</div>
        </el-upload>
      </div>

      <!-- 模型列表 -->
      <div class="model-list-section">
        <h3>可用模型列表</h3>
        <el-table :data="modelList" style="width: 100%" size="small">
          <el-table-column prop="name" label="模型名称" width="250"></el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button 
                type="primary" 
                size="mini" 
                :disabled="scope.row.name === currentModel"
                @click="setCurrentModel(scope.row.name)">
                设为当前模型
              </el-button>
              <el-button 
                type="danger" 
                size="mini" 
                :disabled="scope.row.name === currentModel"
                @click="deleteModel(scope.row.name)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 重新加载按钮 -->
      <div class="reload-section">
        <el-button type="success" @click="reloadModel">重新加载模型</el-button>
        <div class="reload-tip">更换模型后点击此按钮使模型生效</div>
      </div>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ModelManagement',
  data() {
    return {
      currentModel: '',
      modelList: []
    }
  },
  created() {
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
          this.$message.error('获取当前模型失败')
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
          this.$message.error('获取模型列表失败')
        })
    },
    handleModelSuccess(response, file) {
      // eslint-disable-next-line no-unused-vars
      const _response = response
      // eslint-disable-next-line no-unused-vars
      const _file = file
      this.$message.success('模型上传成功')
      this.loadModelData()
    },
    handleModelError(error) {
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
.model-management {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #f0f2f5 100%);
  min-height: calc(100vh - 100px);
  margin: -20px;
  border-radius: 12px;
}

.model-card {
  margin-bottom: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08) !important;
  border: none;
}

.card-header {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 24px;
  position: relative;
  padding-left: 14px;
}

.card-header::before {
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

.current-model-section {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 8px;
  margin: 20px 0;
  border: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.current-model-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.current-model-value {
  font-size: 18px;
  font-weight: 600;
  color: #409EFF;
}

.upload-section,
.model-list-section,
.reload-section {
  margin: 30px 0;
}

h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 20px;
  font-weight: 600;
}

.model-uploader {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.upload-tip,
.reload-tip {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
}

.model-list-section {
  margin-top: 30px;
}

.el-table {
  margin-top: 10px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.reload-section {
  margin-top: 30px;
  text-align: center;
}
</style> 