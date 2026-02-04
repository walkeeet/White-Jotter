<template>
  <el-card class="admin-header">
    <a href="/index">
      <img src="../../assets/img/icon/icon2.png" alt="" width="55px" style="float: left;margin-top: -5px;">
    </a>
    <span style="font-size: 32px;font-weight: bold;position:absolute;left: 100px">白  卷</span>
    <el-dropdown style="float: right;margin-right: 10px;" @command="handleCommand">
      <span class="el-dropdown-link">
        <i class="el-icon-user-solid" style="font-size: 24px;vertical-align: middle;"></i>
        <span style="vertical-align: middle;margin-left: 5px;">{{username}}</span>
        <i class="el-icon-arrow-down el-icon--right"></i>
      </span>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item command="favorites">我的收藏</el-dropdown-item>
        <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </el-card>
</template>

<script>
  import {createRouter} from '../../router'

  export default {
    name: 'Header',
    computed: {
      username () {
        return this.$store.state.username
      }
    },
    methods: {
      handleCommand (command) {
        if (command === 'logout') {
          this.logout()
        } else if (command === 'favorites') {
          this.$router.push('/admin/user/favorites')
        }
      },
      logout () {
        var _this = this
        this.$axios.get('/logout').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.$store.commit('logout')
            _this.$router.replace('/index')
            // 清空路由，防止路由重复加载
            const newRouter = createRouter()
            _this.$router.matcher = newRouter.matcher
          }
        }).catch(failResponse => {})
      }
    }
  }
</script>

<style scoped>
  .admin-header {
    height: 80px;
    opacity: 0.85;
    line-height: 40px;
    min-width: 900px;
  }
  .el-icon-switch-button {
    cursor: pointer;
    outline:0;
  }
  .el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
  }
  .el-icon-arrow-down {
    font-size: 12px;
  }
</style>
