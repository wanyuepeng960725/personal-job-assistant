<template>
  <div class="interview">
    <el-card class="interview-card">
      <template #header>
        <div class="card-header">
          <span>求职进度管理</span>
          <el-button type="primary" @click="showAddDialog">添加面试</el-button>
        </div>
      </template>

      <!-- 搜索筛选 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="公司名称">
          <el-input v-model="searchForm.company" placeholder="请输入公司名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待面试" :value="InterviewStatus.PENDING" />
            <el-option label="已完成" :value="InterviewStatus.COMPLETED" />
            <el-option label="通过" :value="InterviewStatus.PASSED" />
            <el-option label="未通过" :value="InterviewStatus.FAILED" />
            <el-option label="已取消" :value="InterviewStatus.CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 面试列表 -->
      <el-table :data="interviewList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="company" label="公司" width="150" />
        <el-table-column prop="position" label="职位" width="150" />
        <el-table-column prop="interviewDate" label="面试时间" width="180" />
        <el-table-column prop="location" label="面试地点" width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="result" label="结果" width="150" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" size="small" @click="handleComplete(row)">完成</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSearch"
        @current-change="handleSearch"
        class="pagination"
      />
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form :model="interviewForm" :rules="interviewRules" ref="interviewFormRef" label-width="100px">
        <el-form-item label="公司名称" prop="company">
          <el-input v-model="interviewForm.company" placeholder="请输入公司名称" />
        </el-form-item>
        <el-form-item label="职位" prop="position">
          <el-input v-model="interviewForm.position" placeholder="请输入职位" />
        </el-form-item>
        <el-form-item label="面试时间" prop="interviewDate">
          <el-date-picker
            v-model="interviewForm.interviewDate"
            type="datetime"
            placeholder="选择面试时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="面试地点" prop="location">
          <el-input v-model="interviewForm.location" placeholder="请输入面试地点" />
        </el-form-item>
        <el-form-item label="备注" prop="notes">
          <el-input
            v-model="interviewForm.notes"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 完成面试对话框 -->
    <el-dialog v-model="completeDialogVisible" title="完成面试" width="500px">
      <el-form :model="completeForm" ref="completeFormRef" label-width="100px">
        <el-form-item label="面试结果" prop="result">
          <el-select v-model="completeForm.result" placeholder="请选择结果">
            <el-option label="通过" value="通过" />
            <el-option label="未通过" value="未通过" />
            <el-option label="待定" value="待定" />
          </el-select>
        </el-form-item>
        <el-form-item label="反馈备注" prop="notes">
          <el-input
            v-model="completeForm.notes"
            type="textarea"
            :rows="3"
            placeholder="请输入反馈备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitComplete" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { InterviewStatus, type Interview, type CreateInterviewRequest } from '@/types'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const completeDialogVisible = ref(false)
const dialogTitle = ref('添加面试')
const interviewFormRef = ref<FormInstance>()
const completeFormRef = ref<FormInstance>()

const searchForm = reactive({
  company: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const interviewList = ref<Interview[]>([])

const interviewForm = reactive<CreateInterviewRequest>({
  company: '',
  position: '',
  interviewDate: '',
  location: '',
  notes: ''
})

const completeForm = reactive({
  id: 0,
  result: '',
  notes: ''
})

const interviewRules: FormRules = {
  company: [{ required: true, message: '请输入公司名称', trigger: 'blur' }],
  position: [{ required: true, message: '请输入职位', trigger: 'blur' }],
  interviewDate: [{ required: true, message: '请选择面试时间', trigger: 'blur' }],
  location: [{ required: true, message: '请输入面试地点', trigger: 'blur' }]
}

const getStatusType = (status: InterviewStatus) => {
  const typeMap: Record<InterviewStatus, any> = {
    [InterviewStatus.PENDING]: 'warning',
    [InterviewStatus.COMPLETED]: 'info',
    [InterviewStatus.PASSED]: 'success',
    [InterviewStatus.FAILED]: 'danger',
    [InterviewStatus.CANCELLED]: 'info'
  }
  return typeMap[status] || ''
}

const getStatusText = (status: InterviewStatus) => {
  const textMap: Record<InterviewStatus, string> = {
    [InterviewStatus.PENDING]: '待面试',
    [InterviewStatus.COMPLETED]: '已完成',
    [InterviewStatus.PASSED]: '通过',
    [InterviewStatus.FAILED]: '未通过',
    [InterviewStatus.CANCELLED]: '已取消'
  }
  return textMap[status] || status
}

const handleSearch = async () => {
  loading.value = true
  try {
    const { getInterviewListAPI } = await import('@/api/index')
    const response = await getInterviewListAPI({
      company: searchForm.company,
      status: searchForm.status,
      page: pagination.page,
      pageSize: pagination.pageSize
    })
    interviewList.value = response.data.list
    pagination.total = response.data.total
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchForm.company = ''
  searchForm.status = ''
  handleSearch()
}

const showAddDialog = () => {
  dialogTitle.value = '添加面试'
  Object.assign(interviewForm, {
    company: '',
    position: '',
    interviewDate: '',
    location: '',
    notes: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: Interview) => {
  dialogTitle.value = '编辑面试'
  Object.assign(interviewForm, {
    company: row.company,
    position: row.position,
    interviewDate: row.interviewDate,
    location: row.location,
    notes: row.notes || ''
  })
  dialogVisible.value = true
}

const handleDialogClose = () => {
  interviewFormRef.value?.resetFields()
}

const handleSubmit = async () => {
  if (!interviewFormRef.value) return

  await interviewFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const { createInterviewAPI, updateInterviewAPI } = await import('@/api/index')
        if (dialogTitle.value === '添加面试') {
          await createInterviewAPI(interviewForm)
        } else {
          await updateInterviewAPI(completeForm.id, interviewForm)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        handleSearch()
      } catch (error) {
        ElMessage.error('操作失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleComplete = (row: Interview) => {
  completeForm.id = row.id
  completeForm.result = ''
  completeForm.notes = row.notes || ''
  completeDialogVisible.value = true
}

const handleSubmitComplete = async () => {
  submitLoading.value = true
  try {
    const { completeInterviewAPI } = await import('@/api/index')
    await completeInterviewAPI(completeForm.id, {
      result: completeForm.result,
      notes: completeForm.notes
    })
    ElMessage.success('已标记为完成')
    completeDialogVisible.value = false
    handleSearch()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row: Interview) => {
  ElMessageBox.confirm('确定要删除这条面试记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const { deleteInterviewAPI } = await import('@/api/index')
      await deleteInterviewAPI(row.id)
      ElMessage.success('删除成功')
      handleSearch()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.interview {
  height: 100%;
}

.interview-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
