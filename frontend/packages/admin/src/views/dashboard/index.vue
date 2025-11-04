<template>
  <div class="dashboard-container">
    <el-row :gutter="20" class="dashboard-header">
      <el-col :span="6">
        <el-card class="dashboard-card">
          <div class="card-content">
            <div class="card-icon" style="background-color: #409eff">
              <User />
            </div>
            <div class="card-info">
              <div class="card-title">用户总数</div>
              <div class="card-value">1,234</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="dashboard-card">
          <div class="card-content">
            <div class="card-icon" style="background-color: #67c23a">
              <UserFilled />
            </div>
            <div class="card-info">
              <div class="card-title">在线用户</div>
              <div class="card-value">128</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="dashboard-card">
          <div class="card-content">
            <div class="card-icon" style="background-color: #e6a23c">
              <Tickets />
            </div>
            <div class="card-info">
              <div class="card-title">角色数量</div>
              <div class="card-value">24</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="dashboard-card">
          <div class="card-content">
            <div class="card-icon" style="background-color: #f56c6c">
              <Menu />
            </div>
            <div class="card-info">
              <div class="card-title">菜单数量</div>
              <div class="card-value">42</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="dashboard-content">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>用户增长趋势</span>
            </div>
          </template>
          <div class="chart-container">
            <div ref="chartRef" style="height: 300px"></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <span>系统信息</span>
            </div>
          </template>
          <div class="system-info">
            <el-descriptions :column="1" size="small">
              <el-descriptions-item label="系统名称">权限管理系统</el-descriptions-item>
              <el-descriptions-item label="版本">v1.0.0</el-descriptions-item>
              <el-descriptions-item label="Vue版本">3.3.4</el-descriptions-item>
              <el-descriptions-item label="Element Plus版本">2.3.8</el-descriptions-item>
              <el-descriptions-item label="服务器时间">{{ currentTime }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { User, UserFilled, Tickets, Menu } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const chartRef = ref()
let chartInstance: echarts.ECharts | null = null

// 当前时间
const currentTime = ref(new Date().toLocaleString())

// 更新时间
const timer = setInterval(() => {
  currentTime.value = new Date().toLocaleString()
}, 1000)

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return

  chartInstance = echarts.init(chartRef.value)

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: [120, 200, 150, 80, 70, 110, 130],
        type: 'line',
        smooth: true,
        areaStyle: {}
      }
    ]
  }

  chartInstance.setOption(option)
}

// 窗口大小变化时重置图表
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  clearInterval(timer)
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
  }
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  .dashboard-header {
    margin-bottom: 20px;

    .dashboard-card {
      .card-content {
        display: flex;
        align-items: center;

        .card-icon {
          width: 60px;
          height: 60px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 20px;

          svg {
            width: 30px;
            height: 30px;
            color: white;
          }
        }

        .card-info {
          .card-title {
            font-size: 14px;
            color: var(--el-text-color-secondary);
            margin-bottom: 5px;
          }

          .card-value {
            font-size: 24px;
            font-weight: bold;
            color: var(--el-text-color-primary);
          }
        }
      }
    }
  }

  .dashboard-content {
    .chart-card,
    .info-card {
      .card-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
      }

      .chart-container {
        width: 100%;
      }

      .system-info {
        :deep(.el-descriptions__label) {
          width: 100px;
        }
      }
    }
  }
}
</style>