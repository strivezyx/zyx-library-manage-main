<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getReaderCategoryAPI } from '@/api/readerCategory'
import { addReaderAPI, deleteReadersAPI, getReaderPageAPI } from '@/api/reader'

interface Reader {
  id: number
  name: string
  categoryId: number
  sex: number
  wAddress: string
  hAddress: string
  phone: string
  email: string
  createTime: string
  notes: string
}

interface ReaderDTO {
  name: string
  categoryId: number
  sex: number
  wAddress: string
  hAddress: string
  phone: string
  email: string
  notes: string
}

interface Category {
  id: number
  name: string
}

const router = useRouter()
const dialogFormVisible = ref(false)
const formLabelWidth = '120px'

const formRef = ref<FormInstance>()
const form = reactive<ReaderDTO>({
  name: '',
  categoryId: 1,
  sex: 1,
  wAddress: '',
  hAddress: '',
  phone: '',
  email: '',
  notes: '',
})

const rules: FormRules<ReaderDTO> = {
  name: [{ required: true, trigger: 'blur', message: '请输入姓名' }],
  categoryId: [{ required: true, trigger: 'change', message: '请选择分类' }],
  sex: [{ required: true, trigger: 'change', message: '请选择性别' }],
  wAddress: [{ required: true, trigger: 'blur', message: '请输入工作单位' }],
  hAddress: [{ required: true, trigger: 'blur', message: '请输入家庭住址' }],
  phone: [
    { required: true, trigger: 'blur', message: '请输入手机号' },
    { pattern: /^1[3-9]\d{9}$/, trigger: 'blur', message: '手机号格式不正确' },
  ],
  email: [{ required: true, trigger: 'blur', message: '请输入邮箱' }],
  notes: [{ required: true, trigger: 'blur', message: '请输入备注' }],
}

const pageData = reactive({
  name: '',
  categoryId: '' as number | '',
  page: 1,
  pageSize: 6,
})

const categoryList = ref<Category[]>([])
const readerList = ref<Reader[]>([])
const total = ref(0)
const multiSelection = ref<Reader[]>([])

const sexList = [
  { id: 1, name: '男' },
  { id: 0, name: '女' },
]

const resetForm = () => {
  Object.assign(form, {
    name: '',
    categoryId: 1,
    sex: 1,
    wAddress: '',
    hAddress: '',
    phone: '',
    email: '',
    notes: '',
  })
}

const getCategoryName = (categoryId: number) => {
  return categoryList.value.find((item) => item.id === categoryId)?.name || '-'
}

const loadCategories = async () => {
  const { data: res } = await getReaderCategoryAPI({ page: 1, pageSize: 100 })
  categoryList.value = res.data.records || []
}

const loadReaders = async () => {
  const { data: res } = await getReaderPageAPI(pageData)
  readerList.value = res.data.records || []
  total.value = res.data.total || 0
}

const openAddDialog = () => {
  resetForm()
  dialogFormVisible.value = true
}

const addReader = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const { data: res } = await addReaderAPI(form)
  if (res.code !== 0) return

  dialogFormVisible.value = false
  ElMessage.success('新增读者成功')
  await loadReaders()
}

const searchReaders = async () => {
  pageData.page = 1
  await loadReaders()
}

const handleCurrentChange = async () => {
  await loadReaders()
}

const handleSizeChange = async () => {
  pageData.page = 1
  await loadReaders()
}

const handleSelectionChange = (val: Reader[]) => {
  multiSelection.value = val
}

const goUpdate = (row: Reader) => {
  router.push({ name: 'readerUpdate', query: { id: row.id } })
}

const deleteSingle = async (row: Reader) => {
  await ElMessageBox.confirm('是否删除该读者？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })

  const { data: res } = await deleteReadersAPI(String(row.id))
  if (res.code !== 0) return

  ElMessage.success('删除成功')
  await loadReaders()
}

const deleteBatch = async () => {
  if (!multiSelection.value.length) {
    ElMessage.warning('请先选择要删除的读者')
    return
  }

  await ElMessageBox.confirm('是否批量删除选中的读者？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })

  const ids = multiSelection.value.map((item) => item.id).join(',')
  const { data: res } = await deleteReadersAPI(ids)
  if (res.code !== 0) return

  ElMessage.success('批量删除成功')
  await loadReaders()
}

loadCategories()
loadReaders()
</script>

<template>
  <el-dialog v-model="dialogFormVisible" title="新增读者" width="520">
    <el-form ref="formRef" :model="form" :rules="rules">
      <el-form-item label="姓名" :label-width="formLabelWidth" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="分类" :label-width="formLabelWidth" prop="categoryId">
        <el-select v-model="form.categoryId" placeholder="请选择读者分类" style="width: 100%">
          <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="性别" :label-width="formLabelWidth" prop="sex">
        <el-select v-model="form.sex" placeholder="请选择性别" style="width: 100%">
          <el-option v-for="item in sexList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="工作单位" :label-width="formLabelWidth" prop="wAddress">
        <el-input v-model="form.wAddress" />
      </el-form-item>
      <el-form-item label="家庭住址" :label-width="formLabelWidth" prop="hAddress">
        <el-input v-model="form.hAddress" />
      </el-form-item>
      <el-form-item label="手机号" :label-width="formLabelWidth" prop="phone">
        <el-input v-model="form.phone" />
      </el-form-item>
      <el-form-item label="邮箱" :label-width="formLabelWidth" prop="email">
        <el-input v-model="form.email" />
      </el-form-item>
      <el-form-item label="备注" :label-width="formLabelWidth" prop="notes">
        <el-input v-model="form.notes" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogFormVisible = false">取消</el-button>
      <el-button type="primary" @click="addReader">确定</el-button>
    </template>
  </el-dialog>

  <el-card class="my-card">
    <div class="toolbar">
      <el-input v-model="pageData.name" placeholder="请输入读者姓名" style="width: 180px" />
      <el-select v-model="pageData.categoryId" clearable placeholder="读者分类" style="width: 180px">
        <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
      </el-select>
      <el-button type="success" @click="searchReaders">查询</el-button>
      <el-button type="danger" @click="deleteBatch">批量删除</el-button>
      <el-button type="primary" @click="openAddDialog">新增读者</el-button>
    </div>

    <el-table :data="readerList" stripe border @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="52" />
      <el-table-column prop="id" label="读者号" width="90" />
      <el-table-column prop="name" label="姓名" width="110" />
      <el-table-column label="分类" width="120">
        <template #default="scope">
          {{ getCategoryName(scope.row.categoryId) }}
        </template>
      </el-table-column>
      <el-table-column label="性别" width="80">
        <template #default="scope">{{ scope.row.sex === 1 ? '男' : '女' }}</template>
      </el-table-column>
      <el-table-column prop="wAddress" label="工作单位" min-width="140" />
      <el-table-column prop="hAddress" label="家庭住址" min-width="140" />
      <el-table-column prop="phone" label="电话" width="130" />
      <el-table-column prop="email" label="邮箱" min-width="160" />
      <el-table-column prop="createTime" label="创建时间" min-width="165" />
      <el-table-column prop="notes" label="备注" min-width="140" />
      <el-table-column label="操作" width="170" fixed="right">
        <template #default="scope">
          <el-button type="primary" @click="goUpdate(scope.row)">修改</el-button>
          <el-button type="danger" @click="deleteSingle(scope.row)">删除</el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无数据" />
      </template>
    </el-table>

    <el-pagination
      class="pagination"
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      :page-sizes="[2, 4, 6, 8]"
      v-model:current-page="pageData.page"
      v-model:page-size="pageData.pageSize"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
    />
  </el-card>
</template>

<style lang="less" scoped>
.toolbar {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.pagination {
  margin-top: 16px;
  justify-content: center;
}
</style>
