<template>
  <div style="margin-top: 40px">
    <div class="notes-area">
      <el-card style="text-align: left">
        <div slot="header" style="display: flex; justify-content: space-between; align-items: center;">
          <span>我的笔记</span>
          <el-button type="primary" size="small" @click="createNote">新建笔记</el-button>
        </div>
        <div v-if="notes.length === 0" style="text-align: center; padding: 40px; color: #909399;">
          <p>暂无笔记，点击上方按钮创建新笔记</p>
        </div>
        <div v-for="note in notes" :key="note.id" class="note-item">
          <div style="float:left;width:85%;height: 120px;">
            <router-link class="note-link" :to="{path:'/note/editor', query:{id: note.id}}">
              <span style="font-size: 18px"><strong>{{ note.title }}</strong></span>
            </router-link>
            <el-divider content-position="left">{{ formatTime(note.updateTime) }}</el-divider>
            <p class="note-abstract">{{ note.noteAbstract || '暂无摘要' }}</p>
          </div>
          <div style="float: right; margin-top: 30px;">
            <el-button type="primary" size="small" @click="editNote(note.id)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteNote(note.id)">删除</el-button>
          </div>
          <el-divider></el-divider>
        </div>
      </el-card>
    </div>
    <el-pagination
      v-if="total > pageSize"
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
  name: 'MyNotes',
  data () {
    return {
      notes: [],
      pageSize: 10,
      total: 0,
      currentPage: 1
    }
  },
  mounted () {
    this.loadNotes()
  },
  methods: {
    loadNotes () {
      this.$axios.get(`/user/notes/${this.pageSize}/${this.currentPage}`).then(resp => {
        if (resp && resp.data.code === 200) {
          this.notes = resp.data.result.content
          this.total = resp.data.result.totalElements
        } else {
          this.$message.error(resp.data.message || '加载笔记失败')
          if (resp.data.message === '请先登录') {
            this.$router.push('/login')
          }
        }
      })
    },
    formatTime (time) {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleString('zh-CN')
    },
    createNote () {
      this.$router.push('/note/editor')
    },
    editNote (id) {
      this.$router.push({ path: '/note/editor', query: { id: id } })
    },
    deleteNote (id) {
      this.$confirm('确定要删除此笔记吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.delete(`/user/note/${id}`).then(resp => {
          if (resp && resp.data.code === 200) {
            this.$message.success('删除成功')
            this.loadNotes()
          } else {
            this.$message.error(resp.data.message || '删除失败')
          }
        })
      }).catch(() => {})
    },
    handleCurrentChange (page) {
      this.currentPage = page
      this.loadNotes()
    }
  }
}
</script>

<style scoped>
.notes-area {
  width: 990px;
  margin-left: auto;
  margin-right: auto;
}

.note-item {
  padding: 10px 0;
}

.note-link {
  text-decoration: none;
  color: #606266;
}

.note-link:hover {
  color: #409EFF;
}

.note-abstract {
  color: #909399;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>
