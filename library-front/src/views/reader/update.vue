<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { getReaderCategoryAPI } from '@/api/readerCategory'
import { getReaderByIdAPI, updateReaderAPI } from '@/api/reader'

interface ReaderDTO {
  id?: number
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
const route = useRoute()
const formRef = ref<FormInstance>()
const formLabelWidth = '120px'

const categoryList = ref<Category[]>([])
const sexList = [
  { id: 1, name: '男' },
  { id: 0, name: '女' },
]

const form = reactive<ReaderDTO>({
  id: undefined,
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

const cancel = () => {
  router.push({ path: '/reader' })
}

const submit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const { data: res } = await updateReaderAPI(form)
  if (res.code !== 0) return

  ElMessage.success('修改成功')
  router.push({ path: '/reader' })
}

const init = async () => {
  const { data: categoryRes } = await getReaderCategoryAPI({ page: 1, pageSize: 100 })
  categoryList.value = categoryRes.data.records || []

  const readerId = Number(route.query.id)
  if (!readerId) return

  const { data: readerRes } = await getReaderByIdAPI(readerId)
  Object.assign(form, readerRes.data)
}

init()
</script>

<template>
  <el-card class="my-card">
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

    <el-button type="success" @click="submit">保存</el-button>
    <el-button plain type="info" @click="cancel">取消</el-button>
  </el-card>
</template>
