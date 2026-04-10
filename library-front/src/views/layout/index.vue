<script setup lang="ts" name="layout">
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useManagerStore } from '@/store'

const managerStore = useManagerStore()
const router = useRouter()

const menuList = [
  { title: '数据统计', path: '/home', icon: 'PieChart' },
  { title: '图书分类', path: '/bookCategory', icon: 'Memo' },
  { title: '图书列表', path: '/book', icon: 'Collection' },
  { title: '读者分类', path: '/readerCategory', icon: 'Postcard' },
  { title: '读者列表', path: '/reader', icon: 'User' },
  {
    title: '借书还书',
    path: '/lendReturn',
    icon: 'Reading',
    children: [
      { subpath: '/lendReturn/add', title: '新增借书', icon: 'Operation' },
      { subpath: '/lendReturn', title: '记录展示/还书', icon: 'Tickets' },
    ],
  },
  { title: '个人设置', path: '/manager', icon: 'Setting' },
]

const quitFn = async () => {
  try {
    await ElMessageBox.confirm('确认退出登录？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    managerStore.clearAuth()
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch {
    ElMessage.info('已取消退出')
  }
}
</script>

<template>
  <div class="common-layout">
    <el-container>
      <el-header>
        <h1 class="h1">图书管理系统</h1>
        <el-dropdown>
          <el-button type="primary">
            {{ managerStore.managerInfo ? managerStore.managerInfo.name : '未登录' }}
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="quitFn">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-container class="sidebar">
        <el-menu
          default-active="/home"
          class="el-menu-vertical-demo"
          background-color="#556677"
          text-color="#fff"
          active-text-color="#ffd04b"
          unique-opened
          router
        >
          <template v-for="item in menuList" :key="item.path">
            <el-menu-item v-if="!item.children" :index="item.path">
              <el-icon>
                <component :is="item.icon" />
              </el-icon>
              <span>{{ item.title }}</span>
            </el-menu-item>

            <el-sub-menu v-else :index="item.path">
              <template #title>
                <el-icon>
                  <component :is="item.icon" />
                </el-icon>
                <span>{{ item.title }}</span>
              </template>
              <el-menu-item v-for="obj in item.children" :key="obj.subpath" :index="obj.subpath">
                <el-icon>
                  <component :is="obj.icon" />
                </el-icon>
                <span>{{ obj.title }}</span>
              </el-menu-item>
            </el-sub-menu>
          </template>
        </el-menu>

        <el-container class="mycontainer">
          <el-main>
            <router-view />
          </el-main>
          <el-footer>© 2026 Design and Implementation of a Library Management System Based on SpringBoot</el-footer>
        </el-container>
      </el-container>
    </el-container>
  </div>
</template>

<style lang="less" scoped>
.common-layout {
  height: 100%;
  background-color: #eee;
}

.el-header {
  position: relative;
  background-color: #334455;
  color: #eeeeee;
  text-align: center;
  line-height: 60px;
  height: 8vh;
}

.h1 {
  display: inline-block;
  margin: 0;
  text-align: center;
  font-size: 24px;
}

.sidebar {
  display: flex;
  height: 92vh;
}

.el-dropdown {
  position: absolute;
  right: 0;

  .el-button {
    margin: 14px 40px;
    background-color: #22aaff;
    border-color: #22aaff;
    color: #fff;
  }
}

.el-menu {
  width: 200px;
  background-color: #445566;
}

.mycontainer {
  display: flex;
  flex: 6;
  flex-direction: column;
}

.el-main {
  flex: 1;
  padding: 0;
  background-color: #eeeeee;
  color: #333;
}

.el-footer {
  background-color: #ddd;
  font-size: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
