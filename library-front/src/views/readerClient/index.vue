<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { useManagerStore } from '@/store'
import {
  getMyBorrowPageAPI,
  getReaderBookPageAPI,
  getReaderProfileAPI,
  returnMyBorrowAPI,
  updateReaderPasswordAPI,
  updateReaderProfileAPI,
} from '@/api/readerClient'

interface Book {
  id: number
  name: string
  author: string
  press: string
  status: number
  notes: string
}

interface Borrow {
  id: number
  bId: number
  lendDate: string
  returnDate: string
  status: number
  notes: string
}

const store = useManagerStore()
const router = useRouter()

const activeTab = ref('books')

const bookQuery = reactive({
  name: '',
  page: 1,
  pageSize: 8,
})
const books = ref<Book[]>([])
const bookTotal = ref(0)

const borrowQuery = reactive({
  notes: '',
  page: 1,
  pageSize: 8,
})
const borrows = ref<Borrow[]>([])
const borrowTotal = ref(0)

const profileFormRef = ref<FormInstance>()
const profileForm = reactive({
  phone: '',
  email: '',
  notes: '',
  wAddress: '',
  hAddress: '',
})

const emailValidator = (_rule: any, value: string, callback: (err?: Error) => void) => {
  if (!value) {
    callback()
    return
  }
  const pattern = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/
  if (!pattern.test(value)) {
    callback(new Error('邮箱格式不正确'))
    return
  }
  callback()
}

const profileRules: FormRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' },
  ],
  email: [{ validator: emailValidator, trigger: 'blur' }],
}

const pwdFormRef = ref<FormInstance>()
const pwdForm = reactive({
  oldPwd: '',
  newPwd: '',
  confirmPwd: '',
})

const samePwd = (_rule: any, value: string, callback: (err?: Error) => void) => {
  if (value !== pwdForm.newPwd) {
    callback(new Error('两次输入的新密码不一致'))
    return
  }
  callback()
}

const pwdRules: FormRules = {
  oldPwd: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPwd: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { pattern: /^\S{6,15}$/, message: '新密码必须是6-15位非空字符', trigger: 'blur' },
  ],
  confirmPwd: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: samePwd, trigger: 'blur' },
  ],
}

const loadBooks = async () => {
  const { data: res } = await getReaderBookPageAPI(bookQuery)
  books.value = res.data.records || []
  bookTotal.value = res.data.total || 0
}

const loadBorrows = async () => {
  const { data: res } = await getMyBorrowPageAPI(borrowQuery)
  borrows.value = (res.data.records || []).sort((a: Borrow, b: Borrow) => b.id - a.id)
  borrowTotal.value = res.data.total || 0
}

const loadProfile = async () => {
  const { data: res } = await getReaderProfileAPI()
  if (res.code !== 0) return

  profileForm.phone = res.data.phone || ''
  profileForm.email = res.data.email || ''
  profileForm.notes = res.data.notes || ''
  profileForm.wAddress = res.data.wAddress || ''
  profileForm.hAddress = res.data.hAddress || ''
}

const searchBooks = async () => {
  bookQuery.page = 1
  await loadBooks()
}

const searchBorrows = async () => {
  borrowQuery.page = 1
  await loadBorrows()
}

const returnBook = async (row: Borrow) => {
  await ElMessageBox.confirm('确认自助还书？', '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  })

  const { data: res } = await returnMyBorrowAPI(row.id)
  if (res.code !== 0) return

  ElMessage.success('还书成功')
  await loadBorrows()
  await loadBooks()
}

const updateProfile = async () => {
  const valid = await profileFormRef.value?.validate().catch(() => false)
  if (!valid) return

  const { data: res } = await updateReaderProfileAPI({
    phone: profileForm.phone,
    email: profileForm.email,
    notes: profileForm.notes,
    wAddress: profileForm.wAddress,
    hAddress: profileForm.hAddress,
  })
  if (res.code !== 0) return

  if (store.readerInfo) {
    store.setReader({ ...store.readerInfo, phone: profileForm.phone })
  }
  ElMessage.success('个人信息已更新')
}

const changePassword = async () => {
  const valid = await pwdFormRef.value?.validate().catch(() => false)
  if (!valid) return

  const { data: res } = await updateReaderPasswordAPI({ oldPwd: pwdForm.oldPwd, newPwd: pwdForm.newPwd })
  if (res.code !== 0) return

  ElMessage.success('密码修改成功，请重新登录')
  store.clearAuth()
  router.push('/login')
}

const quitFn = async () => {
  await ElMessageBox.confirm('确认退出登录？', '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  })
  store.clearAuth()
  router.push('/login')
}

loadBooks()
loadBorrows()
loadProfile()
</script>

<template>
  <div class="reader-center">
    <header class="header">
      <h1>读者中心</h1>
      <div class="user-box">
        <span>{{ store.readerInfo?.name }} ({{ store.readerInfo?.phone }})</span>
        <el-button type="danger" plain @click="quitFn">退出登录</el-button>
      </div>
    </header>

    <el-card>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="个人信息" name="profile">
          <el-form
            ref="profileFormRef"
            :model="profileForm"
            :rules="profileRules"
            label-width="110px"
            class="profile-form"
          >
            <el-form-item label="账号(手机号)" prop="phone">
              <el-input v-model="profileForm.phone" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" />
            </el-form-item>
            <el-form-item label="家庭地址" prop="hAddress">
              <el-input v-model="profileForm.hAddress" />
            </el-form-item>
            <el-form-item label="工作地址" prop="wAddress">
              <el-input v-model="profileForm.wAddress" />
            </el-form-item>
            <el-form-item label="备注" prop="notes">
              <el-input v-model="profileForm.notes" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateProfile">保存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="可借图书" name="books">
          <div class="toolbar">
            <el-input v-model="bookQuery.name" placeholder="按书名搜索" style="width: 220px" />
            <el-button type="primary" @click="searchBooks">查询</el-button>
          </div>
          <el-table :data="books" stripe border>
            <el-table-column prop="id" label="编号" width="90" />
            <el-table-column prop="name" label="书名" min-width="180" />
            <el-table-column prop="author" label="作者" width="140" />
            <el-table-column prop="press" label="出版社" min-width="180" />
            <el-table-column prop="notes" label="备注" min-width="180" />
            <template #empty>
              <el-empty description="暂无可借图书" />
            </template>
          </el-table>
          <el-pagination
            class="pagination"
            background
            layout="total, sizes, prev, pager, next"
            :total="bookTotal"
            :page-sizes="[4, 8, 12]"
            v-model:current-page="bookQuery.page"
            v-model:page-size="bookQuery.pageSize"
            @current-change="loadBooks"
            @size-change="loadBooks"
          />
        </el-tab-pane>

        <el-tab-pane label="我的借阅" name="borrows">
          <div class="toolbar">
            <el-input v-model="borrowQuery.notes" placeholder="按备注搜索" style="width: 220px" />
            <el-button type="primary" @click="searchBorrows">查询</el-button>
          </div>
          <el-table :data="borrows" stripe border>
            <el-table-column prop="id" label="记录号" width="90" />
            <el-table-column prop="bId" label="图书编号" width="100" />
            <el-table-column prop="lendDate" label="借出日期" width="140" />
            <el-table-column prop="returnDate" label="归还日期" width="140" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 0" type="warning">借阅中</el-tag>
                <el-tag v-else type="success">已归还</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="notes" label="备注" min-width="160" />
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="scope">
                <el-button
                  type="success"
                  :disabled="scope.row.status !== 0"
                  @click="returnBook(scope.row)"
                >
                  自助还书
                </el-button>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无借阅记录" />
            </template>
          </el-table>
          <el-pagination
            class="pagination"
            background
            layout="total, sizes, prev, pager, next"
            :total="borrowTotal"
            :page-sizes="[4, 8, 12]"
            v-model:current-page="borrowQuery.page"
            v-model:page-size="borrowQuery.pageSize"
            @current-change="loadBorrows"
            @size-change="loadBorrows"
          />
        </el-tab-pane>

        <el-tab-pane label="修改密码" name="password">
          <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="90px" class="pwd-form">
            <el-form-item label="旧密码" prop="oldPwd">
              <el-input type="password" v-model="pwdForm.oldPwd" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPwd">
              <el-input type="password" v-model="pwdForm.newPwd" />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPwd">
              <el-input type="password" v-model="pwdForm.confirmPwd" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="changePassword">确认修改</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<style scoped>
.reader-center {
  min-height: 100vh;
  padding: 24px;
  background: #f4f6f8;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.user-box {
  display: flex;
  align-items: center;
  gap: 12px;
}

.toolbar {
  margin-bottom: 12px;
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 12px;
  justify-content: center;
}

.profile-form {
  max-width: 520px;
  margin-top: 12px;
}

.pwd-form {
  max-width: 460px;
  margin-top: 12px;
}
</style>
