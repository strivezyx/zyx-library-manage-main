<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { loginAPI } from '@/api/manager'
import { readerLoginAPI } from '@/api/readerClient'
import { useManagerStore } from '@/store'

const router = useRouter()
const store = useManagerStore()
const loginRef = ref<FormInstance>()
const role = ref<'manager' | 'reader'>('manager')

const form = ref({
  account: '',
  password: '',
})

const rules: FormRules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^\S{6,15}$/, message: '密码必须是6-15位非空字符', trigger: 'blur' },
  ],
}

const loginFn = async () => {
  const valid = await loginRef.value?.validate().catch(() => false)
  if (!valid) return

  if (role.value === 'manager') {
    const { data: res } = await loginAPI({ name: form.value.account, password: form.value.password })
    if (res.code !== 0) return
    store.setManager(res.data)
    ElMessage.success('管理员登录成功')
    router.push('/')
  } else {
    const { data: res } = await readerLoginAPI({ phone: form.value.account, password: form.value.password })
    if (res.code !== 0) return
    store.setReader(res.data)
    ElMessage.success('读者登录成功')
    router.push('/reader-client')
  }
}
</script>

<template>
  <div class="background">
    <el-form label-width="0" class="login-box" :model="form" :rules="rules" ref="loginRef">
      <div class="title-box">图书管理系统登录</div>

      <el-radio-group v-model="role" class="role-switch">
        <el-radio-button label="manager">管理员</el-radio-button>
        <el-radio-button label="reader">读者</el-radio-button>
      </el-radio-group>

      <el-form-item prop="account">
        <el-input
          v-model="form.account"
          :placeholder="role === 'manager' ? '请输入管理员账号' : '请输入读者手机号'"
        />
      </el-form-item>
      <el-form-item prop="password">
        <el-input type="password" v-model="form.password" placeholder="请输入密码" />
      </el-form-item>
      <el-form-item class="my-el-form-item">
        <el-button type="primary" class="btn-login" @click="loginFn">登录</el-button>
        <el-link v-if="role === 'manager'" type="info" @click="$router.push('/reg')">管理员注册</el-link>
        <el-link v-else type="info" @click="$router.push('/reader-reg')">读者注册</el-link>
      </el-form-item>
    </el-form>
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

.login-box {
  z-index: 10;
  width: 420px;
  min-height: 360px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  padding: 0 30px 20px;
  box-sizing: border-box;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: #aaaaaa 0 0 60px;

  .title-box {
    height: 90px;
    line-height: 90px;
    font-size: 22px;
    font-weight: 700;
    text-align: center;
  }

  .role-switch {
    width: 100%;
    display: flex;
    justify-content: center;
    margin-bottom: 18px;
  }

  .btn-login {
    width: 100%;
  }
}
</style>

<style>
.el-form {
  display: flex;
  flex-direction: column;
}
</style>
