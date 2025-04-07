<template>
  <div class="statistics-container">
    <!-- 数据统计卡片 -->
    <el-row :gutter="20" class="data-overview">
      <el-col :span="8">
        <el-card shadow="hover" class="data-card">
          <div class="data-header">
            <i class="el-icon-date"></i>
            <span>今日处理</span>
          </div>
          <div class="data-content">{{ statistics.today || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="data-card">
          <div class="data-header">
            <i class="el-icon-date"></i>
            <span>本周处理</span>
          </div>
          <div class="data-content">{{ statistics.week || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="data-card">
          <div class="data-header">
            <i class="el-icon-date"></i>
            <span>本月处理</span>
          </div>
          <div class="data-content">{{ statistics.month || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 标记分类统计 -->
    <el-card class="chart-card">
      <div slot="header" class="chart-header">
        <span>标记分类统计</span>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          @change="handleDateChange">
        </el-date-picker>
      </div>
      <div class="chart-container" ref="markingsChart"></div>
    </el-card>

    <!-- 处理统计 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header" class="chart-header">
            <span>处理趋势</span>
          </div>
          <div class="chart-container" ref="lineChart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header" class="chart-header">
            <span>处理数量</span>
          </div>
          <div class="chart-container" ref="barChart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import * as echarts from 'echarts'

export default {
  name: 'DataStatistics',
  data() {
    return {
      dateRange: [],
      chart: null,
      lineChart: null,
      barChart: null
    }
  },
  computed: {
    ...mapState({
      statistics: state => state.statistics
    })
  },
  methods: {
    ...mapActions(['fetchStatistics']),
    initCharts() {
      if (this.chart) {
        this.chart.dispose()
      }
      if (this.lineChart) {
        this.lineChart.dispose()
      }
      if (this.barChart) {
        this.barChart.dispose()
      }
      this.chart = echarts.init(this.$refs.markingsChart)
      this.lineChart = echarts.init(this.$refs.lineChart)
      this.barChart = echarts.init(this.$refs.barChart)
      this.updateCharts()
    },
    updateCharts() {
      this.updateChart() // 更新饼图
      this.updateProcessingCharts() // 更新处理统计图表
    },
    updateProcessingCharts() {
      if (!this.statistics.images || !Array.isArray(this.statistics.images)) return

      // 按日期分组统计处理次数
      const dateMap = new Map()
      this.statistics.images.forEach(item => {
        const date = item.createTime.split('T')[0]
        dateMap.set(date, (dateMap.get(date) || 0) + 1)
      })

      // 按日期排序
      const sortedData = Array.from(dateMap.entries())
        .sort((a, b) => a[0].localeCompare(b[0]))

      const dates = sortedData.map(([date]) => date)
      const counts = sortedData.map(([, count]) => count)

      // 配置线图
      const lineOption = {
        title: {
          text: '处理趋势',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: dates,
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          minInterval: 1
        },
        series: [{
          data: counts,
          type: 'line',
          smooth: true,
          symbolSize: 10,
          lineStyle: {
            width: 4,
            shadowColor: 'rgba(0, 0, 0, 0.1)',
            shadowBlur: 10
          },
          areaStyle: {
            opacity: 0.3,
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
            ])
          },
          itemStyle: {
            borderWidth: 2,
            shadowColor: 'rgba(0, 0, 0, 0.1)',
            shadowBlur: 10
          }
        }]
      }

      // 配置柱图
      const barOption = {
        title: {
          text: '处理数量',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: dates,
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          minInterval: 1
        },
        series: [{
          data: counts,
          type: 'bar',
          barWidth: '60%',
          itemStyle: {
            borderRadius: [5, 5, 0, 0],
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#409EFF' },
              { offset: 1, color: '#67C23A' }
            ]),
            shadowColor: 'rgba(0, 0, 0, 0.1)',
            shadowBlur: 10
          }
        }]
      }

      this.lineChart && this.lineChart.setOption(lineOption)
      this.barChart && this.barChart.setOption(barOption)
    },
    initChart() {
      if (this.chart) {
        this.chart.dispose()
      }
      this.chart = echarts.init(this.$refs.markingsChart)
      this.updateChart()
    },
    updateChart() {
      if (!this.chart) return

      // 处理标记数据
      const markingsMap = new Map()
      if (this.statistics.images && Array.isArray(this.statistics.images)) {
        this.statistics.images.forEach(item => {
          if (item.markings) {
            const markings = item.markings.split(',')
            markings.forEach(marking => {
              const trimmedMarking = marking.trim()
              if (trimmedMarking) {
                markingsMap.set(trimmedMarking, (markingsMap.get(trimmedMarking) || 0) + 1)
              }
            })
          }
        })
      }

      // 转换为图表数据
      const data = Array.from(markingsMap).map(([name, value]) => ({ name, value }))

      // 设置图表配置
      const option = {
        title: {
          text: '标记类型统计',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center'
        },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: true,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2,
              shadowBlur: 10,
              shadowColor: 'rgba(0, 0, 0, 0.1)'
            },
            label: {
              show: true,
              formatter: '{b}: {c}',
              fontSize: 14,
              fontWeight: 'bold'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 16,
                fontWeight: 'bold',
                color: '#409EFF'
              },
              itemStyle: {
                shadowBlur: 20,
                shadowColor: 'rgba(0, 0, 0, 0.2)'
              }
            },
            data: data.length > 0 ? data : [{ name: '暂无数据', value: 1 }]
          }
        ]
      }

      this.chart.setOption(option)
    },
    handleDateChange(val) {
      const params = {}
      if (val) {
        params.begin = val[0]
        params.end = val[1]
      }
      this.fetchStatistics(params)
    },
    handleResize() {
      if (this.chart) {
        this.chart.resize()
      }
      if (this.lineChart) {
        this.lineChart.resize()
      }
      if (this.barChart) {
        this.barChart.resize()
      }
    }
  },
  mounted() {
    this.fetchStatistics()
    this.$nextTick(() => {
      this.initCharts()
    })
    window.addEventListener('resize', this.handleResize)
  },
  watch: {
    statistics: {
      deep: true,
      handler() {
        this.$nextTick(() => {
          this.updateCharts()
        })
      }
    }
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose()
      this.chart = null
    }
    if (this.lineChart) {
      this.lineChart.dispose()
      this.lineChart = null
    }
    if (this.barChart) {
      this.barChart.dispose()
      this.barChart = null
    }
    window.removeEventListener('resize', this.handleResize)
  }
}
</script>

<style scoped>
.statistics-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.data-overview {
  margin-bottom: 25px;
}

.data-card {
  height: 180px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.data-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.data-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.data-card:hover::before {
  opacity: 1;
}

.data-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  font-size: 16px;
  color: #606266;
  position: relative;
  z-index: 1;
}

.data-header i {
  margin-right: 12px;
  font-size: 24px;
  color: #409EFF;
  background: linear-gradient(135deg, #409EFF, #67C23A);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  transition: transform 0.3s ease;
}

.data-card:hover .data-header i {
  transform: scale(1.1);
}

.data-content {
  font-size: 36px;
  font-weight: bold;
  background: linear-gradient(135deg, #409EFF, #67C23A);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  position: relative;
  z-index: 1;
}

.chart-card {
  margin-bottom: 25px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.chart-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}

.chart-header span {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  position: relative;
}

.chart-header span::after {
  content: '';
  position: absolute;
  bottom: -16px;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.chart-card:hover .chart-header span::after {
  transform: scaleX(1);
}

.chart-container {
  height: 400px;
  width: 100%;
  padding: 20px;
}

.chart-row {
  margin-top: 25px;
}

/* 日期选择器样式优化 */
:deep(.el-date-editor) {
  border-radius: 4px;
}

:deep(.el-date-editor:hover) {
  border-color: #409EFF;
}

:deep(.el-date-editor.is-active) {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

/* 为图表添加渐变背景 */
:deep(.echarts) {
  border-radius: 8px;
  background: linear-gradient(135deg, #ffffff, #f8f9fa);
}

/* 添加响应式间距 */
@media screen and (max-width: 1400px) {
  .chart-container {
    height: 350px;
  }
  
  .data-card {
    height: 160px;
  }
}

@media screen and (max-width: 1200px) {
  .chart-container {
    height: 300px;
  }
  
  .data-card {
    height: 140px;
  }
  
  .data-content {
    font-size: 32px;
  }
}
</style> 