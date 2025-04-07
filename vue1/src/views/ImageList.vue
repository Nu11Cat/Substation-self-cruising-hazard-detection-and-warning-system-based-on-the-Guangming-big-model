<template>
  <div class="image-list">
    <el-card>
      <div slot="header" class="list-header">
        <div class="header-left">
          <span>日志列表</span>
          <el-tag type="info" effect="plain" class="record-count">
            共 {{ total }} 条记录
          </el-tag>
        </div>
        <div class="header-right">
          <el-radio-group v-model="viewMode" size="small">
            <el-radio-button label="table">表格模式</el-radio-button>
            <el-radio-button label="card">卡片模式</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form" size="small">
        <el-form-item label="名称" class="search-form-label">
          <el-input v-model="searchForm.name" placeholder="请输入名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="标记" class="search-form-label">
          <el-input v-model="searchForm.markings" placeholder="请输入标记" clearable></el-input>
        </el-form-item>
        <el-form-item label="创建用户" class="search-form-label">
          <el-input v-model="searchForm.createUser" placeholder="请输入创建用户" clearable></el-input>
        </el-form-item>
        <el-form-item label="时间范围" class="search-form-label">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            :picker-options="pickerOptions"
            @change="handleDateChange">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" icon="el-icon-search">查询</el-button>
          <el-button @click="resetSearch" icon="el-icon-refresh">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格视图 -->
      <template v-if="viewMode === 'table'">
        <el-table
          v-loading="loading"
          :data="imageList"
          style="width: 100%"
          :header-cell-style="{ background: '#f8fafc', color: '#333' }">
          <el-table-column prop="id" label="ID" width="80" sortable></el-table-column>
          <el-table-column prop="name" label="名称" width="150" show-overflow-tooltip></el-table-column>
          <el-table-column label="处理前图片" width="180">
            <template slot-scope="scope">
              <div class="table-image-wrapper">
                <el-image
                  :src="scope.row.perUrl"
                  :preview-src-list="[scope.row.perUrl]"
                  fit="contain">
                  <div slot="error" class="image-error">
                    <i class="el-icon-picture-outline"></i>
                  </div>
                </el-image>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="处理后图片" width="180">
            <template slot-scope="scope">
              <div class="table-image-wrapper">
                <el-image
                  :src="scope.row.postUrl"
                  :preview-src-list="[scope.row.postUrl]"
                  fit="contain">
                  <div slot="error" class="image-error">
                    <i class="el-icon-picture-outline"></i>
                  </div>
                </el-image>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="markings" label="标记" width="120">
            <template slot-scope="scope">
              <el-tag 
                v-for="(tag, index) in scope.row.markings.split(',')" 
                :key="index"
                size="small"
                :type="getTagType(tag)"
                class="tag-item">
                {{ getTagText(tag) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createUser" label="创建用户" width="120"></el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="160" sortable>
            <template slot-scope="scope">
              {{ formatDateTime(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="updateUser" label="更新人" width="120"></el-table-column>
          <el-table-column prop="updateTime" label="更新时间" width="160" sortable>
            <template slot-scope="scope">
              {{ formatDateTime(scope.row.updateTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="other" label="备注" min-width="150" show-overflow-tooltip>
            <template slot-scope="scope">
              <el-tooltip v-if="scope.row.other" :content="scope.row.other" placement="top">
                <span class="ellipsis">{{ scope.row.other }}</span>
              </el-tooltip>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="primary"
                plain
                icon="el-icon-edit"
                @click="handleEdit(scope.row)">
                编辑
              </el-button>
              <el-button
                size="mini"
                type="danger"
                plain
                icon="el-icon-delete"
                @click="handleDelete(scope.row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </template>

      <!-- 卡片视图 -->
      <template v-else>
        <div class="card-view">
          <el-row :gutter="20">
            <el-col :span="8" v-for="item in imageList" :key="item.id">
              <el-card class="image-card" shadow="hover">
                <div class="card-header">
                  <span class="card-title">{{ item.name }}</span>
                  <div class="card-actions">
                    <el-button type="text" icon="el-icon-edit" @click="handleEdit(item)"></el-button>
                    <el-button type="text" icon="el-icon-delete" class="delete-btn" @click="handleDelete(item)"></el-button>
                  </div>
                </div>
                <div class="card-body">
                  <div class="image-compare">
                    <div class="image-item">
                      <div class="image-label">处理前</div>
                      <el-image
                        :src="item.perUrl"
                        fit="contain"
                        :preview-src-list="[item.perUrl]">
                        <div slot="error" class="image-error">
                          <i class="el-icon-picture-outline"></i>
                        </div>
                      </el-image>
                    </div>
                    <div class="image-item">
                      <div class="image-label">处理后</div>
                      <el-image
                        :src="item.postUrl"
                        fit="contain"
                        :preview-src-list="[item.postUrl]">
                        <div slot="error" class="image-error">
                          <i class="el-icon-picture-outline"></i>
                        </div>
                      </el-image>
                    </div>
                  </div>
                  <div class="card-info">
                    <div class="info-item">
                      <span class="label">标记：</span>
                      <div class="tags">
                        <el-tag 
                          v-for="(tag, index) in item.markings.split(',')" 
                          :key="index"
                          size="small"
                          :type="getTagType(tag)"
                          class="tag-item">
                          {{ getTagText(tag) }}
                        </el-tag>
                      </div>
                    </div>
                    <div class="info-item">
                      <span class="label">创建：</span>
                      <span>{{ item.createUser }} 于 {{ formatDateTime(item.createTime) }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">更新：</span>
                      <span>{{ item.updateUser }} 于 {{ formatDateTime(item.updateTime) }}</span>
                    </div>
                    <div class="info-item" v-if="item.other">
                      <span class="label">备注：</span>
                      <el-tooltip :content="item.other" placement="top">
                        <span class="ellipsis">{{ item.other }}</span>
                      </el-tooltip>
                    </div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </template>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page.sync="currentPage"
          :page-sizes="[5, 10, 20, 50]"
          :page-size.sync="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          background>
        </el-pagination>
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog 
      title="编辑日志信息" 
      :visible.sync="dialogVisible" 
      width="500px"
      :close-on-click-modal="false">
      <el-form :model="editForm" :rules="rules" ref="editForm" label-width="100px" size="small">
        <el-form-item label="名称" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入名称"></el-input>
        </el-form-item>
        <el-form-item label="标记" prop="markings">
          <el-select 
            v-model="editForm.markings" 
            multiple 
            filterable 
            allow-create 
            default-first-option
            placeholder="请选择或输入标记">
            <el-option
              v-for="item in markingOptions"
              :key="item"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="other">
          <el-input 
            v-model="editForm.other" 
            type="textarea" 
            :rows="3"
            placeholder="请输入备注信息">
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import axios from 'axios'
import { formatDate } from '@/utils/date'

export default {
  name: 'ImageList',
  data() {
    return {
      searchForm: {
        name: '',
        markings: '',
        createUser: '',
        begin: '',
        end: ''
      },
      dateRange: [],
      dialogVisible: false,
      editForm: {
        id: null,
        name: '',
        markings: '',
        other: ''
      },
      currentPage: 1,
      pageSize: 5,
      viewMode: 'table',
      saving: false,
      markingOptions: [],
      markingMap: {
        'bj_bpmh': '表盘模糊',
        'bj_bpps': '表盘破损',
        'bj_wkps': '外壳破损',
        'bjdsyc': '表计读数有错',
        'jyz_pl': '绝缘子破裂',
        'sly_dmyw': '地面油污',
        'hxq_gjbs': '硅胶变色',
        'hxq_gjtps': '硅胶体破损',
        'xmbhyc': '箱门闭合异常',
        'yw_gkxfw': '挂空悬浮物',
        'yw_nc': '鸟巢',
        'gbps': '盖板破损',
        'wcaqm': '未穿安全帽',
        'wcgz': '未穿工装',
        'xy': '吸烟'
      },
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setMonth(start.getMonth() - 1)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setMonth(start.getMonth() - 3)
            picker.$emit('pick', [start, end])
          }
        }]
      },
      rules: {
        name: [
          { required: true, message: '请输入名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        markings: [
          { required: true, message: '请至少选择一个标记', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    ...mapState({
      imageList: state => state.imageList,
      total: state => state.total,
      loading: state => state.loading
    })
  },
  created() {
    this.fetchData()
  },
  methods: {
    ...mapActions(['fetchImageList']),
    formatDateTime(datetime) {
      return formatDate(new Date(datetime), 'yyyy-MM-dd HH:mm:ss')
    },
    getTagType(tag) {
      const types = ['', 'success', 'warning', 'danger']
      const index = Math.abs(this.hashCode(tag)) % types.length
      return types[index]
    },
    hashCode(str) {
      let hash = 0
      for (let i = 0; i < str.length; i++) {
        hash = ((hash << 5) - hash) + str.charCodeAt(i)
        hash = hash & hash
      }
      return hash
    },
    handleSearch() {
      this.currentPage = 1
      this.fetchData()
    },
    resetSearch() {
      this.searchForm = {
        name: '',
        markings: '',
        createUser: '',
        begin: '',
        end: ''
      }
      this.dateRange = []
      this.currentPage = 1
      this.fetchData()
    },
    handleDateChange(val) {
      if (val) {
        this.searchForm.begin = val[0]
        this.searchForm.end = val[1]
      } else {
        this.searchForm.begin = ''
        this.searchForm.end = ''
      }
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchData()
    },
    handleEdit(row) {
      this.editForm = {
        ...row,
        markings: row.markings ? row.markings.split(',') : []
      }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确认删除该记录吗？此操作不可恢复', '提示', {
          type: 'warning'
        })
        await axios.delete(`/images/${row.id}`)
        this.$message.success('删除成功')
        this.fetchData()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error)
          this.$message.error('删除失败')
        }
      }
    },
    async handleSave() {
      try {
        await this.$refs.editForm.validate()
        this.saving = true
        const formData = {
          ...this.editForm,
          markings: Array.isArray(this.editForm.markings) 
            ? this.editForm.markings.join(',')
            : this.editForm.markings
        }
        await axios.put('/images', formData)
        this.$message.success('保存成功')
        this.dialogVisible = false
        this.fetchData()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('保存失败:', error)
          this.$message.error('保存失败')
        }
      } finally {
        this.saving = false
      }
    },
    async fetchData() {
      const params = {
        ...this.searchForm,
        page: this.currentPage,
        pageSize: this.pageSize
      }
      await this.fetchImageList(params)
    },
    getTagText(tag) {
      if (!tag) return '';
      const tagName = tag.split(' with confidence ')[0].trim();
      return this.markingMap[tagName] || tagName;
    }
  }
}
</script>

<style scoped>
.image-list {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #f0f2f5 100%);
  min-height: calc(100vh - 100px);
  margin: -20px;
  border-radius: 12px;
}

.el-card {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08) !important;
  border: none;
}

.list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.header-left span {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  position: relative;
  padding-left: 14px;
  white-space: nowrap;
}

.header-left span::before {
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

.record-count {
  font-size: 14px;
  margin-left: 12px;
  padding: 4px 12px;
  white-space: nowrap;
}

.search-form {
  margin-bottom: 24px;
  padding: 24px;
  background: #f8fafc;
  border-radius: 8px;
  position: relative;
  transition: all 0.3s ease;
}

.search-form-label {
  font-size: 15px;
}

.search-form-label :deep(.el-form-item__label) {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.search-form :deep(.el-input__inner) {
  font-size: 14px;
  height: 36px;
  line-height: 36px;
}

.search-form :deep(.el-button) {
  font-size: 14px;
  padding: 10px 20px;
}

.el-table {
  border-radius: 8px;
  overflow: hidden;
  font-size: 14px;
}

.el-table::before {
  display: none;
}

.el-table th {
  background: #f8fafc !important;
  color: #333;
  font-weight: 600;
  font-size: 15px;
}

.el-table td, .el-table th {
  padding: 12px 0;
  font-size: 14px;
}

.tag-item {
  margin: 2px;
  font-size: 13px;
}

.image-error {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f8fafc;
  color: #909399;
}

.image-error i {
  font-size: 24px;
  margin-bottom: 8px;
}

.image-error::after {
  content: '暂无图片';
  font-size: 12px;
  color: #909399;
}

/* 卡片视图样式 */
.card-view {
  margin: 0 -10px;
}

.image-card {
  margin-bottom: 20px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.image-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12) !important;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
}

.card-title {
  font-weight: 600;
  color: #333;
  font-size: 16px;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.delete-btn {
  color: #f56c6c;
}

.card-body {
  padding: 16px;
}

.image-compare {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.image-item {
  flex: 1;
}

.image-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.image-item .el-image {
  width: 100%;
  height: 120px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
  background: #f8fafc;
  overflow: hidden;
}

.image-item .el-image img {
  object-fit: contain !important;
  width: 100%;
  height: 100%;
}

.card-info {
  font-size: 14px;
}

.info-item {
  margin-bottom: 8px;
  display: flex;
  align-items: flex-start;
}

.info-item .label {
  color: #606266;
  width: 60px;
  flex-shrink: 0;
  font-size: 14px;
}

.info-item .tags {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.pagination-container {
  margin-top: 24px;
  padding: 16px 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.el-pagination {
  padding: 0;
  font-size: 14px;
}

.el-pagination :deep(.el-pagination__total),
.el-pagination :deep(.el-pagination__jump) {
  font-size: 14px;
}

/* 响应式调整 */
@media screen and (max-width: 1400px) {
  .search-form .el-form-item {
    margin-bottom: 16px;
  }
  
  .el-col-8 {
    width: 50%;
  }
}

@media screen and (max-width: 1200px) {
  .el-table .el-button--mini {
    padding: 5px 8px;
  }
  
  .el-col-8 {
    width: 100%;
  }
}

@media screen and (max-width: 768px) {
  .header-left {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .record-count {
    margin-left: 12px;
  }
}

/* 表格中的图片样式 */
.table-image-wrapper {
  width: 150px;
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f8fafc;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.table-image-wrapper .el-image {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.3s ease;
}

.table-image-wrapper:hover .el-image {
  transform: scale(1.05);
}

/* 图片预览相关样式 */
:deep(.el-image-viewer__wrapper) {
  background-color: rgba(0, 0, 0, 0.8);
}

:deep(.el-image-viewer__btn) {
  background-color: rgba(0, 0, 0, 0.3);
}

:deep(.el-image-viewer__close) {
  color: #fff;
}

:deep(.el-image-viewer__canvas) {
  background: #1c1c1c;
}

/* 编辑对话框样式 */
.el-dialog :deep(.el-dialog__title) {
  font-size: 18px;
}

.el-dialog :deep(.el-form-item__label) {
  font-size: 15px;
}

.el-dialog :deep(.el-input__inner),
.el-dialog :deep(.el-textarea__inner) {
  font-size: 14px;
}

.el-dialog :deep(.el-button) {
  font-size: 14px;
}
</style> 