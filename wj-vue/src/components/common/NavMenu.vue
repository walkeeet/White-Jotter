<template>
  <div>
    <el-menu
      :default-active="currentPath"
      router
      mode="horizontal"
      background-color="white"
      text-color="#222"
      active-text-color="red"
      style="min-width: 1300px">
      <el-menu-item index="/index">首页</el-menu-item>
      <el-menu-item index="/jotter">笔记本</el-menu-item>
      <el-menu-item index="/library">图书馆</el-menu-item>
      
      <el-submenu index="/user" v-if="$store.state.username" style="position:absolute;right: 25%">
        <template slot="title">
          <i class="el-icon-user"></i>
          {{ $store.state.username }}
        </template>
        <el-menu-item index="/user/collection">我的收藏</el-menu-item>
        <el-menu-item index="/admin/dashboard">管理中心</el-menu-item>
        <el-menu-item index="/logout" @click="logout">退出登录</el-menu-item>
      </el-submenu>
      
      <el-menu-item index="/login" v-else style="position:absolute;right: 25%">登录</el-menu-item>
      
      <span style="position: absolute;padding-top: 20px;right: 43%;font-size: 20px;font-weight: bold">White Jotter - Your Mind Palace</span>
      <el-input
        placeholder="快速搜索..."
        prefix-icon="el-icon-search"
        size="medium"
        style="width: 300px;position:absolute;margin-top: 12px;right: 18%"
        v-model="keywords">
      </el-input>
    </el-menu>
  </div>
</template>

<script>
  export default {
    name: 'NavMenu',
    data () {
      return {
        keywords: ''
      }
    },
    computed: {
      hoverBackground () {
        return '#ffd04b'
      },
      currentPath () {
        var x = this.$route.path.indexOf('/', 1)
        if (x !== -1) {
          return this.$route.path.substring(0, x)
        } else {
          return this.$route.path
        }
      }
    },
    methods: {
      logout () {
        this.$store.commit('logout')
        this.$axios.post('/logout').then(resp => {
          this.$message.success('已退出登录')
          this.$router.push('/index')
        })
      }
    }
  }
</script>

<style scoped>
  a{
    text-decoration: none;
  }

  span {
    pointer-events: none;
  }

  .el-menu--horizontal > .el-submenu {
    float: none;
  }
</style>
