<template>
  <div class="article-comments">
    <el-card style="text-align: left; margin-top: 20px;">
      <div slot="header">
        <span style="font-size: 16px; font-weight: bold;">
          评论 ({{ total }})
        </span>
      </div>

      <!-- 评论输入框 -->
      <div class="comment-input" v-if="isLoggedIn">
        <div class="current-user">
          <span>发表评论</span>
        </div>
        <el-input
          type="textarea"
          :rows="3"
          v-model="newComment"
          placeholder="写下你的评论..."
          maxlength="500"
          show-word-limit>
        </el-input>
        <div class="submit-btn">
          <el-button type="primary" size="small" @click="submitComment">发表评论</el-button>
        </div>
      </div>
      <div v-else class="login-tip">
        <el-button type="text" @click="goToLogin">登录后可发表评论</el-button>
      </div>

      <el-divider></el-divider>

      <!-- 评论列表 -->
      <div class="comment-list">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <!-- 一级评论 -->
          <div class="comment-main">
            <div class="comment-header">
              <span class="username">{{ comment.user ? comment.user.username : '未知用户' }}</span>
              <span class="time">{{ formatTime(comment.createTime) }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-actions">
              <el-button type="text" size="mini" @click="showReplyInput(comment)">回复</el-button>
              <el-button
                v-if="canDelete(comment)"
                type="text"
                size="mini"
                style="color: #F56C6C"
                @click="deleteComment(comment.id)">
                删除
              </el-button>
            </div>
          </div>

          <!-- 回复输入框 -->
          <div v-if="replyTo === comment.id" class="reply-input">
            <el-input
              type="textarea"
              :rows="2"
              v-model="replyContent"
              :placeholder="'回复 ' + (comment.user ? comment.user.username : '') + '...'"
              maxlength="500"
              show-word-limit>
            </el-input>
            <div class="reply-actions">
              <el-button size="mini" @click="cancelReply">取消</el-button>
              <el-button type="primary" size="mini" @click="submitReply(comment)">回复</el-button>
            </div>
          </div>

          <!-- 二级回复列表 -->
          <div v-if="comment.replies && comment.replies.length > 0" class="reply-list">
            <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
              <div class="reply-header">
                <span class="username">{{ reply.user ? reply.user.username : '未知用户' }}</span>
                <span v-if="reply.replyToUser" class="reply-to">
                  回复 <span class="username">{{ reply.replyToUser.username }}</span>
                </span>
                <span class="time">{{ formatTime(reply.createTime) }}</span>
              </div>
              <div class="reply-content">{{ reply.content }}</div>
              <div class="reply-actions">
                <el-button type="text" size="mini" @click="showReplyInput(comment, reply)">回复</el-button>
                <el-button
                  v-if="canDelete(reply)"
                  type="text"
                  size="mini"
                  style="color: #F56C6C"
                  @click="deleteComment(reply.id)">
                  删除
                </el-button>
              </div>
            </div>
          </div>

          <el-divider></el-divider>
        </div>
      </div>

      <!-- 分页 -->
      <el-pagination
        v-if="total > pageSize"
        background
        layout="prev, pager, next"
        :page-size="pageSize"
        :total="total"
        @current-change="handlePageChange">
      </el-pagination>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'ArticleComments',
  props: {
    articleId: {
      type: Number,
      required: true
    }
  },
  data () {
    return {
      comments: [],
      newComment: '',
      replyContent: '',
      replyTo: null,
      replyToUserId: null,
      page: 1,
      pageSize: 10,
      total: 0,
      currentUser: null
    }
  },
  computed: {
    isLoggedIn () {
      return this.$store.state.username && this.$store.state.username !== ''
    }
  },
  mounted () {
    this.loadComments()
    this.loadCurrentUser()
  },
  methods: {
    loadComments () {
      var _this = this
      this.$axios.get('/comments/' + this.articleId + '/' + this.pageSize + '/' + this.page).then(resp => {
        if (resp && resp.data.code === 200) {
          _this.comments = resp.data.result.content
          _this.total = resp.data.result.totalElements
        }
      })
    },
    loadCurrentUser () {
      if (this.isLoggedIn) {
        var _this = this
        this.$axios.get('/user/' + this.$store.state.username).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.currentUser = resp.data.result
          }
        })
      }
    },
    submitComment () {
      if (!this.newComment.trim()) {
        this.$message.warning('请输入评论内容')
        return
      }
      var _this = this
      this.$axios.post('/comments', {
        articleId: this.articleId,
        content: this.newComment,
        parentId: null
      }).then(resp => {
        if (resp && resp.data.code === 200) {
          _this.$message.success('评论成功')
          _this.newComment = ''
          _this.page = 1
          _this.loadComments()
        } else {
          _this.$message.error(resp.data.message || '评论失败')
        }
      }).catch(() => {
        _this.$message.error('评论失败，请检查是否已登录')
      })
    },
    showReplyInput (parentComment, replyToComment) {
      if (!this.isLoggedIn) {
        this.goToLogin()
        return
      }
      this.replyTo = parentComment.id
      if (replyToComment) {
        this.replyToUserId = replyToComment.userId
      } else {
        this.replyToUserId = parentComment.userId
      }
      this.replyContent = ''
    },
    cancelReply () {
      this.replyTo = null
      this.replyToUserId = null
      this.replyContent = ''
    },
    submitReply (parentComment) {
      if (!this.replyContent.trim()) {
        this.$message.warning('请输入回复内容')
        return
      }
      var _this = this
      this.$axios.post('/comments', {
        articleId: this.articleId,
        content: this.replyContent,
        parentId: parentComment.id,
        replyToUserId: this.replyToUserId
      }).then(resp => {
        if (resp && resp.data.code === 200) {
          _this.$message.success('回复成功')
          _this.cancelReply()
          _this.loadComments()
        } else {
          _this.$message.error(resp.data.message || '回复失败')
        }
      }).catch(() => {
        _this.$message.error('回复失败，请检查是否已登录')
      })
    },
    deleteComment (commentId) {
      this.$confirm('确定删除这条评论吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        var _this = this
        this.$axios.delete('/comments/' + commentId).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.$message.success('删除成功')
            _this.loadComments()
          } else {
            _this.$message.error(resp.data.message || '删除失败')
          }
        })
      }).catch(() => {})
    },
    canDelete (comment) {
      return this.currentUser && comment.userId === this.currentUser.id
    },
    handlePageChange (page) {
      this.page = page
      this.loadComments()
    },
    goToLogin () {
      this.$router.push('/login')
    },
    formatTime (time) {
      if (!time) return ''
      const date = new Date(time)
      return date.getFullYear() + '-' +
        String(date.getMonth() + 1).padStart(2, '0') + '-' +
        String(date.getDate()).padStart(2, '0') + ' ' +
        String(date.getHours()).padStart(2, '0') + ':' +
        String(date.getMinutes()).padStart(2, '0')
    }
  }
}
</script>

<style scoped>
.article-comments {
  width: 990px;
  margin: 0 auto;
}

.comment-input {
  margin-bottom: 20px;
}

.current-user {
  margin-bottom: 10px;
  color: #606266;
  font-size: 14px;
}

.submit-btn {
  margin-top: 10px;
  text-align: right;
}

.login-tip {
  text-align: center;
  padding: 20px;
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  margin-bottom: 15px;
}

.comment-main {
  padding: 10px 0;
}

.comment-header,
.reply-header {
  margin-bottom: 8px;
}

.username {
  font-weight: bold;
  color: #409EFF;
  margin-right: 10px;
}

.reply-to {
  color: #606266;
  margin-right: 10px;
}

.time {
  color: #909399;
  font-size: 12px;
}

.comment-content,
.reply-content {
  color: #303133;
  line-height: 1.6;
  margin: 8px 0;
}

.comment-actions,
.reply-actions {
  margin-top: 8px;
}

.reply-input {
  margin: 10px 0 10px 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.reply-actions {
  margin-top: 10px;
  text-align: right;
}

.reply-list {
  margin-left: 20px;
  padding-left: 15px;
  border-left: 2px solid #e4e7ed;
}

.reply-item {
  padding: 10px 0;
}
</style>
