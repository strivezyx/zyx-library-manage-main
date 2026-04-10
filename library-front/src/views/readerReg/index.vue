<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { readerRegisterAPI } from '@/api/readerClient'

const router = useRouter()
const formRef = ref<FormInstance>()

const form = reactive({
  name: '',
  phone: '',
  password: '',
  confirmPwd: '',
  email: '',
  notes: '',
  wAddress: '',
  hAddress: '',
})

const samePwd = (_rule: any, value: string, callback: (err?: Error) => void) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
    return
  }
  callback()
}

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

const rules: FormRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^\S{6,15}$/, message: '密码必须是6-15位非空字符', trigger: 'blur' },
  ],
  confirmPwd: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: samePwd, trigger: 'blur' },
  ],
  email: [{ validator: emailValidator, trigger: 'blur' }],
}

const register = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const { data: res } = await readerRegisterAPI({
    name: form.name,
    phone: form.phone,
    password: form.password,
    email: form.email,
    notes: form.notes,
    wAddress: form.wAddress,
    hAddress: form.hAddress,
  })
  if (res.code !== 0) return

  ElMessage.success('注册成功，请登录')
  router.push('/login')
}
</script>

<template>
  <div class="background">
    <div class="reg-box">
      <div class="title-box">读者注册</div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="账号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="form.password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPwd">
          <el-input type="password" v-model="form.confirmPwd" placeholder="请确认密码" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="家庭地址" prop="hAddress">
          <el-input v-model="form.hAddress" placeholder="请输入家庭地址" />
        </el-form-item>
        <el-form-item label="工作地址" prop="wAddress">
          <el-input v-model="form.wAddress" placeholder="请输入工作地址" />
        </el-form-item>
        <el-form-item label="备注" prop="notes">
          <el-input v-model="form.notes" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="btn-reg" @click="register">注册</el-button>
          <el-link type="info" @click="router.push('/login')">去登录</el-link>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style lang="less" scoped>
.background {
  width: 100%;
  height: 100vh;
  background-size: cover;
  background-image: url('../../assets/library.jpg');
}

.background::before {
  content: '';
  position: absolute;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.6);
  z-index: 1;
}

.reg-box {
  z-index: 10;
  width: 520px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  padding: 24px 30px 16px;
  box-sizing: border-box;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: #aaaaaa 0 0 60px;

  .title-box {
    height: 50px;
    line-height: 50px;
    font-size: 20px;
    font-weight: 700;
    text-align: center;
  }

  .btn-reg {
    width: 100%;
  }
}
</style>
