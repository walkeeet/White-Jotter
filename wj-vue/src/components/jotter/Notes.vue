<template>
  <div style="margin-top: 40px">
    <div class="notes-header">
      <el-button type="primary" v-if="isLogin" @click="goToEditor">发布笔记</el-button>
    </div>
    <div class="notes-area">
      <el-card style="text-align: left">
        <div v-for="note in notes" :key="note.id">
          <div style="float:left;width:85%;height: 150px;">
            <router-link class="note-link" :to="{path:'/note/detail',query:{id: note.id}}">
              <span style="font-size: 20px"><strong>{{note.title}}</strong></span>
            </router-link>
            <el-divider content-position="left">
              <span>{{note.createTime}}</span>
              <span class="comment-count">
                <i class="el-icon-chat-dot-round"></i> {{note.commentCount}}
              </span>
            </el-divider>
            <router-link class="note-link" :to="{path:'/note/detail',query:{id: note.id}}">
              <p>{{note.noteAbstract}}</p>
            </router-link>
          </div>
          <el-image
            style="margin:18px 0 0 30px;width:100px;height: 100px"
            :src="note.cover"
            fit="cover"></el-image>
          <el-divider></el-divider>
        </div>
      </el-card>
    </div>
    <el-pagination
      background
      layout="total, prev, pager, next, jumper"
      @current-change="handleCurrentChange"
      :page-size="pageSize"
      :total="total">
    </el-pagination>
  </div>
</template>

<script>
  export default {
    name: 'Notes',
    data () {
      return {
        notes: [],
        pageSize: 4,
        total: 0,
        isLogin: false
      }
    },
    mounted () {
      this.checkLogin()
      this.loadNotes()
    },
    methods: {
      checkLogin () {
        this.isLogin = !!this.$store.state.user.id
      },
      goToEditor () {
        this.$router.push({ path: '/note/editor' })
      },
      loadNotes () {
        var _this = this
        this.$axios.get('/note/' + this.pageSize + '/1').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.notes = resp.data.result.content
            _this.total = resp.data.result.totalElements
          }
        })
      },
      handleCurrentChange (page) {
        var _this = this
        this.$axios.get('/note/' + this.pageSize + '/' + page).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.notes = resp.data.result.content
            _this.total = resp.data.result.totalElements
          }
        })
      }
    }
  }
</script>

<style scoped>
  .notes-header {
    width: 990px;
    margin: 0 auto 20px auto;
    text-align: right;
  }

  .notes-area {
    width: 990px;
    height: 750px;
    margin-left: auto;
    margin-right: auto;
  }

  .note-link {
    text-decoration: none;
    color: #606266;
  }

  .note-link:hover {
    color: #409EFF;
  }

  .comment-count {
    margin-left: 20px;
    color: #909399;
    font-size: 14px;
  }
</style>
