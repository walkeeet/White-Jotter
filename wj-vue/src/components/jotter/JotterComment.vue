<template>
  <div class="comment-section">
    <el-divider content-position="left">评论 ({{ total }})</el-divider>

    <el-card class="comment-input-card" v-if="isLogin">
      <el-input
        type="textarea"
        :rows="3"
        v-model="newComment"
        placeholder="写下你的评论...">
      </el-input>
      <div class="comment-actions">
        <el-button type="primary" size="small" @click="submitComment" :loading="submitting">发表评论</el-button>
      </div>
    </el-card>
    <el-card class="comment-input-card" v-else>
      <span>请先 <router-link to="/login">登录</router-link> 后发表评论</span>
    </el-card>

    <div class="comment-list">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <el-card class="comment-card">
          <div class="comment-header">
            <strong>{{ comment.user.username }}</strong>
            <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
            <el-button
              type="text"
              size="mini"
              class="delete-btn"
              v-if="isLogin && currentUser === comment.user.username"
              @click="deleteComment(comment.id)">删除</el-button>
          </div>
          <div class="comment-content">{{ comment.content }}</div>
          <div class="comment-footer">
            <el-button type="text" size="small" @click="showReplyInput(comment)">回复</el-button>
          </div>
          <div v-if="replyingCommentId === comment.id" class="reply-input-box">
            <el-input
              type="textarea"
              :rows="2"
              v-model="replyContent"
              :placeholder="'回复 ' + comment.user.username + '...'">
            </el-input>
            <div class="reply-actions">
              <el-button size="small" @click="cancelReply">取消</el-button>
              <el-button type="primary" size="small" @click="submitReply (comment)" :loading="replySubmitting">回复</el-button>
            </div>
          </div>

          <div class="reply-list" v-if="repliesMap[comment.id] && repliesMap[comment.id].length > 0">
            <div v-for="reply in repliesMap[comment.id]" :key="reply.id" class="reply-item">
              <div class="reply-header">
                <strong>{{ reply.user.username }}</strong>
                <span v-if="reply.replyToUser"> 回复 @{{ reply.replyToUser.username }}</span>
                <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                <el-button
                  type="text"
                  size="mini"
                  class="delete-btn"
                  v-if="isLogin && currentUser === reply.user.username"
                  @click="deleteComment(reply.id)">删除</el-button>
              </div>
              <div class="reply-content">{{ reply.content }}</div>
              <el-button type="text" size="mini" @click="showReplyToReply(comment, reply)">回复</el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <div class="pagination" v-if="total > pageSize">
      <el-pagination
        background
        layout="prev, pager, next"
        :page-size="pageSize"
        :total="total"
        :current-page.sync="currentPage"
        @current-change="loadComments">
      </el-pagination>
    </div>
  </div>
</template>

<script>
export default {
  name: 'JotterComment',
  props: {
    articleId: {
      type: Number,
      required: true
    }
  },
  data () {
    return {
      comments: [],
      repliesMap: {},
      total: 0,
      currentPage: 0,
      pageSize: 10,
      newComment: '',
      replyContent: '',
      replyingCommentId: 0,
      replyToUserId: 0,
      submitting: false,
      replySubmitting: false,
      isLogin: false,
      currentUser: ''
    }
  },
  mounted () {
    this.checkLogin()
    this.loadComments()
  },
  methods: {
    checkLogin () {
      if (this.$store.state.user) {
        this.isLogin = true
        this.currentUser = this.$store.state.user.username
      }
    },
    loadComments () {
      const _this = this
      this.$axios.get('/comment/' + this.articleId + '?page=' + (this.currentPage - 1) + '&size=' + this.pageSize).then(resp => {
        if (resp && resp.data.code === 200) {
          _this.comments = resp.data.result.comments.content
          _this.total = resp.data.result.comments.totalElements
          _this.repliesMap = resp.data.result.replies
        }
      })
    },
    submitComment () {
      if (!this.newComment.trim()) {
        this.$message.warning('请输入评论内容')
        return
      }
      this.submitting = true
      const _this = this
      this.$axios.post('/comment/' + this.articleId, { content: this.newComment, parentId: 0 }).then(resp => {
        _this.submitting = false
        if (resp && resp.data.code === 200) {
          _this.$message.success('评论成功')
          _this.newComment = ''
          _this.loadComments()
        } else {
          _this.$message.error(resp.data.message)
        }
      }).catch(() => {
        _this.submitting = false
        _this.$message.error('评论失败，请先登录')
      })
    },
    showReplyInput (comment) {
      if (!this.isLogin) {
        this.$message.warning('请先登录')
        return
      }
      this.replyingCommentId = comment.id
      this.replyToUserId = 0
      this.replyContent = ''
    },
    showReplyToReply (comment, reply) {
      if (!this.isLogin) {
        this.$message.warning('请先登录')
        return
      }
      this.replyingCommentId = comment.id
      this.replyToUserId = reply.user.id
      this.replyContent = ''
    },
    cancelReply () {
      this.replyingCommentId = 0
      this.replyContent = ''
      this.replyToUserId = 0
    },
    submitReply (comment) {
      if (!this.replyContent.trim()) {
        this.$message.warning('请输入回复内容')
        return
      }
      this.replySubmitting = true
      const _this = this
      const commentId = comment.parentId === 0 ? comment.id : comment.parentId
      this.$axios.post('/comment/reply/' + commentId, {
        content: this.replyContent,
        replyToUserId: this.replyToUserId || comment.user.id
      }).then(resp => {
        _this.replySubmitting = false
        if (resp && resp.data.code === 200) {
          _this.$message.success('回复成功')
          _this.cancelReply()
          _this.loadComments()
        } else {
          _this.$message.error(resp.data.message)
        }
      }).catch(() => {
        _this.replySubmitting = false
        _this.$message.error('回复失败')
      })
    },
    deleteComment (commentId) {
      this.$confirm('确定要删除这条评论吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.delete('/comment/' + commentId).then(resp => {
          if (resp && resp.data.code === 200) {
            this.$message.success('删除成功')
            this.loadComments()
          } else {
            this.$message.error(resp.data.message)
          }
        })
      }).catch(() => {})
    },
    formatTime (timestamp) {
      if (!timestamp) return ''
      const date = new Date(timestamp)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hour = String(date.getHours()).padStart(2, '0')
      const minute = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${minute}`
    }
  }
}
</script>

<style scoped>
.comment-section {
  margin-top: 20px;
}

.comment-input-card {
  margin-bottom: 20px;
}

.comment-actions {
  margin-top: 10px;
  text-align: right;
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  margin-bottom: 15px;
}

.comment-card {
  border-radius: 8px;
}

.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.comment-time {
  margin-left: 15px;
  color: #909399;
  font-size: 12px;
}

.delete-btn {
  margin-left: auto;
  color: #f56c6c;
}

.comment-content {
  color: #303133;
  line-height: 1.6;
  margin-bottom: 10px;
}

.comment-footer {
  border-top: 1px solid #ebeef5;
  padding-top: 10px;
  margin-top: 10px;
}

.reply-input-box {
  margin-top: 15px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.reply-actions {
  margin-top: 10px;
  text-align: right;
}

.reply-list {
  margin-top: 15px;
  padding-left: 20px;
  border-left: 3px solid #dcdfe6;
}

.reply-item {
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
  margin-bottom: 10px;
}

.reply-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 13px;
}

.reply-time {
  margin-left: 15px;
  color: #909399;
  font-size: 12px;
}

.reply-content {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 5px;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}
</style>
