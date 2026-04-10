<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, ElTable, type FormRules } from 'element-plus'
import { getBookCategoryAPI } from '@/api/bookCategory'
import { getBookPageAPI, addBookAPI, updateBookStatusAPI, deleteBooksAPI } from '@/api/book'

interface Book {
  id: number
  name: string
  categoryId: number
  author: string
  press: string
  publishDate: string
  price: number
  pageNumber: number
  keywords: string
  createTime: string
  status: number
  notes: string
}

interface BookDTO {
  id?: number
  name: string
  categoryId: number
  author: string
  press: string
  publishDate: string
  price: number
  pageNumber: number
  keywords: string
  notes: string
}

interface Category {
  id: number
  name: string
}

const router = useRouter()
const dialogFormVisible = ref(false)
const formLabelWidth = '140px'

const form = reactive<BookDTO>({
  id: undefined,
  name: '',
  categoryId: 1,
  author: '',
  press: '',
  publishDate: '',
  price: 0,
  pageNumber: 0,
  keywords: '',
  notes: '',
})

const bookList = ref<Book[]>([])
const categoryList = ref<Category[]>([])
const pageData = reactive({
  name: '',
  categoryId: '',
  status: '',
  page: 1,
  pageSize: 6,
})

const isValidForm = ref()
const total = ref(0)
const options = [
  { value: '1', label: '已借出' },
  { value: '0', label: '未借出' },
]

const multiTableRef = ref<InstanceType<typeof ElTable>>()
const multiSelection = ref<Book[]>([])

const idValidator = (_rule: any, value: any, callback: (err?: Error) => void) => {
  if (value === undefined || value === null || value === '') {
    callback()
    return
  }
  if (!/^\d+$/.test(String(value))) {
    callback(new Error('书号必须是数字'))
    return
  }
  callback()
}

const rules: FormRules = {
  id: [{ validator: idValidator, trigger: 'blur' }],
  name: [{ required: true, trigger: 'blur', message: '不能为空' }],
  categoryId: [{ required: true, trigger: 'blur', message: '不能为空' }],
  author: [{ required: true, trigger: 'blur', message: '不能为空' }],
  press: [{ required: true, trigger: 'blur', message: '不能为空' }],
  publishDate: [{ required: true, trigger: 'blur', message: '不能为空' }],
  price: [{ required: true, trigger: 'blur', message: '不能为空' }],
  pageNumber: [{ required: true, trigger: 'blur', message: '不能为空' }],
  keywords: [{ required: true, trigger: 'blur', message: '不能为空' }],
  notes: [{ required: true, trigger: 'blur', message: '不能为空' }],
}

const init = async () => {
  const { data: res } = await getBookCategoryAPI({ page: 1, pageSize: 100 })
  categoryList.value = res.data.records || []
}

const showPageList = async () => {
  const { data: res } = await getBookPageAPI(pageData)
  bookList.value = res.data.records || []
  total.value = res.data.total || 0
}

init()
showPageList()

const handleCurrentChange = () => {
  showPageList()
}

const handleSizeChange = () => {
  showPageList()
}

const handleSelectionChange = (val: Book[]) => {
  multiSelection.value = val
}

const add_btn = async () => {
  try {
    const valid = await isValidForm.value.validate()
    if (!valid) return

    const date = new Date(form.publishDate)
    form.publishDate =
      date.getFullYear() + '-' + String(date.getMonth() + 1).padStart(2, '0') + '-' + String(date.getDate()).padStart(2, '0')

    const { data: res } = await addBookAPI(form)
    if (res.code !== 0) return

    dialogFormVisible.value = false
    ElMessage.success('新增图书成功')
    await showPageList()
  } catch (error) {
    console.error('An error occurred during form validation:', error)
  }
}

const search_btn = async () => {
  pageData.page = 1
  await showPageList()
}

const update_btn = (row: any) => {
  router.push({
    name: 'bookUpdate',
    query: { id: row.id },
  })
}

const change_btn = async (row: any) => {
  await updateBookStatusAPI(row.id)
  ElMessage.success('修改成功')
  await showPageList()
}

const delete_btn = async (row: any) => {
  ElMessageBox.confirm('是否删除该图书？', 'Warning', {
    confirmButtonText: 'OK',
    cancelButtonText: 'Cancel',
    type: 'warning',
  })
    .then(async () => {
      const { data: res } = await deleteBooksAPI(String(row.id))
      if (res.code !== 0) return
      ElMessage.success('删除成功')
      await showPageList()
    })
    .catch(() => {
      ElMessage.info('取消删除')
    })
}

const deleteBatch = (row?: any) => {
  ElMessageBox.confirm('是否批量删除选中的图书？', 'Warning', {
    confirmButtonText: 'OK',
    cancelButtonText: 'Cancel',
    type: 'warning',
  })
    .then(async () => {
      if (row == undefined) {
        if (multiSelection.value.length == 0) {
          ElMessage.warning('请先选择要删除的图书')
          return
        }
        let ids: any = []
        multiSelection.value.map(item => ids.push(item.id))
        const { data: res } = await deleteBooksAPI(ids.join(','))
        if (res.code !== 0) return
      } else {
        const { data: res } = await deleteBooksAPI(String(row.id))
        if (res.code !== 0) return
      }

      ElMessage.success('删除成功')
      await showPageList()
    })
    .catch(() => {
      ElMessage.info('取消删除')
    })
}
</script>

<template>
  <el-dialog v-model="dialogFormVisible" title="添加图书" class="my-info-dialog">
    <el-form :model="form" :rules="rules" ref="isValidForm">
      <el-form-item label="书号" :label-width="formLabelWidth" prop="id">
        <el-input v-model.number="form.id" type="number" autocomplete="off" />
      </el-form-item>
      <el-form-item label="书名" :label-width="formLabelWidth" prop="name">
        <el-input v-model="form.name" autocomplete="off" />
      </el-form-item>
      <el-form-item label="分类" :label-width="formLabelWidth" prop="categoryId">
        <el-select clearable v-model="form.categoryId" placeholder="选择分类类型">
          <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="作者" :label-width="formLabelWidth" prop="author">
        <el-input v-model="form.author" autocomplete="off" />
      </el-form-item>
      <el-form-item label="出版社" :label-width="formLabelWidth" prop="press">
        <el-input v-model="form.press" autocomplete="off" />
      </el-form-item>
      <el-form-item label="出版日期" :label-width="formLabelWidth" prop="publishDate">
        <el-date-picker v-model="form.publishDate" type="date" placeholder="请选择出版日期" style="width: 100%" />
      </el-form-item>
      <el-form-item label="价格" :label-width="formLabelWidth" prop="price">
        <el-input v-model="form.price" autocomplete="off" />
      </el-form-item>
      <el-form-item label="页码数" :label-width="formLabelWidth" prop="pageNumber">
        <el-input v-model="form.pageNumber" autocomplete="off" />
      </el-form-item>
      <el-form-item label="关键词" :label-width="formLabelWidth" prop="keywords">
        <el-input v-model="form.keywords" autocomplete="off" />
      </el-form-item>
      <el-form-item label="备注" :label-width="formLabelWidth" prop="notes">
        <el-input v-model="form.notes" autocomplete="off" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button plain type="info" @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="add_btn()">确认</el-button>
      </div>
    </template>
  </el-dialog>

  <el-card class="my-card">
    <div class="horizontal">
      <el-input style="width: 160px; margin: 20px" v-model="pageData.name" class="input" placeholder="请输入书名" />
      <el-select style="width: 160px; margin: 20px" clearable v-model="pageData.categoryId" placeholder="选择分类类型">
        <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
      </el-select>
      <el-select clearable v-model="pageData.status" placeholder="选择图书状态" style="width: 160px; margin: 20px">
        <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button style="margin: 20px 30px; width:100px" round type="success" @click="search_btn()">查询图书</el-button>
      <el-button style="margin: 20px 20px; width:100px" round type="danger" @click="deleteBatch()">批量删除</el-button>
      <el-button style="margin: 20px 20px; width:100px" type="primary" @click="dialogFormVisible = true">
        <el-icon>
          <Plus style="width: 10em; height: 10em; margin-right: 3px" />
        </el-icon>
        新增图书
      </el-button>
    </div>

    <el-table ref="multiTableRef" :data="bookList" stripe border @selection-change="handleSelectionChange" style="width: 100%">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="书号" />
      <el-table-column prop="name" label="书名" width="100px" />
      <el-table-column prop="categoryId" label="所属分类" width="90">
        <template #default="scope">
          {{ categoryList.find(item => item.id === scope.row.categoryId)?.name || scope.row.categoryId }}
        </template>
      </el-table-column>
      <el-table-column prop="author" label="作者" />
      <el-table-column prop="press" label="出版社" width="150" />
      <el-table-column prop="publishDate" label="出版年" width="130px" />
      <el-table-column prop="price" label="价格" />
      <el-table-column prop="pageNumber" label="页码数" />
      <el-table-column prop="keywords" label="关键词" />
      <el-table-column prop="createTime" label="创建时间" width="150px" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="notes" label="备注" width="200px" />
      <el-table-column label="操作" width="200px" fixed="right">
        <template #default="scope">
          <el-button @click="update_btn(scope.row)" type="primary">修改</el-button>
          <el-button @click="delete_btn(scope.row)" type="danger">删除</el-button>
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
.dialog-footer {
  display: flex;
  justify-content: center;
  margin: 20px 0;

  .el-button {
    width: 150px;
  }
}
</style>

<style>
.el-dialog {
  width: 600px;
  border-radius: 10px;
}

.my-info-dialog .el-dialog__body {
  max-height: 350px;
  overflow: auto;
}
</style>
