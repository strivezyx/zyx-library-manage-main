<script setup lang="ts" name="layout">
import { RouterView, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useManagerStore } from '@/store'

// ------ data ------
const managerStore = useManagerStore()
const menuList = [
  {
    title: '鏁版嵁缁熻',
    path: '/home',
    icon: 'pieChart',
  },
  {
    title: '鍥句功鍒嗙被',
    path: '/bookCategory',
    icon: 'memo',
  },
  {
    title: '鍥句功鍒楄〃',
    path: '/book',
    icon: 'collection',
  },
  {
    title: '璇昏€呭垎绫?,
    path: '/readerCategory',
    icon: 'postcard',
  },
  {
    title: '璇昏€呭垪琛?,
    path: '/reader',
    icon: 'user',
  },
  {
    title: '鍊熶功杩樹功',
    path: '/lendReturn',
    icon: 'reading',
    children: {
      0: {
        subpath: '/lendReturn/add',
        title: '鏂板鍊熶功',
        icon: 'operation',
        children: null
      },
      1: {
        subpath: '/lendReturn',
        title: '璁板綍灞曠ず/杩樹功',
        icon: 'tickets',
        children: null
      }
    }
  },
  // {
  //   title: '娴嬭瘯椤甸潰',
  //   path: '/test',
  //   icon: 'el-icon-s-order',
  // },
  {
    title: '涓汉璁剧疆',
    path: '/manager',
    icon: 'setting',
  },
]

// ------ method ------
const router = useRouter()

const quitFn = () => {
  // 涓轰簡璁╃敤鎴蜂綋楠屾洿濂斤紝鏉ヤ釜纭鎻愮ず妗?  ElMessageBox.confirm(
    '璧颁簡锛岀埍鏄細娑堝け鐨勫悧?',
    '閫€鍑虹櫥褰?,
    {
      confirmButtonText: 'OK',
      cancelButtonText: 'Cancel',
      type: 'warning',
    }
  )
    .then(() => {
      ElMessage({
        type: 'success',
        message: '閫€鍑烘垚鍔?,
      })
      // 娓呴櫎鐢ㄦ埛淇℃伅锛屽寘鎷瑃oken
      managerStore.clearAuth()
      console.log(managerStore)
      router.push('/login')
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '宸插彇娑堥€€鍑?,
      })
    })
}
</script>

<template>
  <div class="common-layout">
    <el-container>
      <el-header>
        <h1 class="h1">鍥?涔?绠?鐞?绯?缁?/h1>
        <el-dropdown>
          <el-button type="primary">
            {{ managerStore.managerInfo ? managerStore.managerInfo.name : '鏈櫥褰? }}
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="quitFn">閫€鍑虹櫥褰?/el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-container class="sidebar">
        <!-- 宸︿晶瀵艰埅鑿滃崟鍖哄煙 -->
        <el-menu default-active="/home" class="el-menu-vertical-demo" background-color="#556677" text-color="#fff"
          active-text-color="#ffd04b" unique-opened router>
          <!-- 鍔犱簡router妯″紡锛屽氨浼氬湪婵€娲诲鑸椂浠?:index 浣滀负path杩涜璺緞璺宠浆锛坣b!涓嶇敤鑷繁鍐欒矾鐢变簡!锛?-->
          <!-- 鏍规嵁涓嶅悓鎯呭喌閫夋嫨menu-item/submenu杩涜閬嶅巻锛屾墍浠ュ灞傚template閬嶅巻锛岄噷闈㈢粍浠跺仛鍒ゆ柇鐪嬫槸鍚﹁娆￠亶鍘嗗埌鑷繁 -->
          <template v-for="item in menuList">
            <el-menu-item v-if="!item.children" :index="item.path" :key="item.path">
              <el-icon>
                <component :is="item.icon" />
              </el-icon>
              <span>{{ item.title }}</span>
            </el-menu-item>
            <el-sub-menu v-else :index="item.path" :key="item.title">
              <!-- 鏈夊瓙鑿滃崟鐨勪晶杈规爮椤?-->
              <template #title>
                <el-icon>
                  <component :is="item.icon" />
                </el-icon>
                <span>{{ item.title }}</span>
              </template>
              <!-- 璇ラ」涓嬬殑瀛愯彍鍗?-->
              <el-menu-item v-for="obj in item.children" :index="obj.subpath" :key="obj.subpath">
                <el-icon>
                  <component :is="obj.icon" />
                </el-icon>
                <span>{{ obj.title }}</span>
              </el-menu-item>
            </el-sub-menu>
          </template>
        </el-menu>
        <!-- 鍙充晶鍐呭鍜屽簳閮ㄦ爮 -->
        <el-container class="mycontainer">
          <el-main>
            <router-view></router-view>
          </el-main>
          <el-footer>漏 2024.5.23 library Tech and Class. All rights reserved.</el-footer>
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
  text-align: center;
  line-height: 80px;
}

a {
  display: block;
  height: 5rem;
  color: rgb(0, 209, 118);
  font-size: 20px;
  font-weight: bold;
  text-decoration: none;
}

a:hover {
  background-color: rgb(87, 123, 108);
  color: #6bffce;
}

.el-footer {
  background-color: #ddd;
  font-size: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>


