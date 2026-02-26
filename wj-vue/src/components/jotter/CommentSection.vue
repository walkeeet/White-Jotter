<template>
  <div class="comment-section">
    <el-divider content-position="left">评论 ({{ total }})</el-divider>

    <div class="comment-input" v-if="isLoggedIn">
      <el-input
        type="textarea"
        :rows="3"
        placeholder="发表评论..."
        v-model="newComment">
      </el-input>
      <el-button type="primary" size="small" @click="submitComment" :loading="submitting" style="margin-top: 10px">
        发表评论
      </el-button>
    </div>
    <div class="login-tip" v-else>
      <el-alert title="请登录后发表评论" type="info" :closable="false" show-icon></el-alert>
    </div>

    <div class="comments-list" style="margin-top: 20px">
      <div class="comment-item" v-for="comment in comments" :key="comment.id">
        <div class="comment-main">
          <div class="comment-header">
            <span class="username">{{ comment.username }}</span>
            <span class="time">{{ formatTime(comment.createTime) }}</span>
            <el-button
              v-if="isLoggedIn && currentUsername === comment.username"
              type="text"
              size="mini"
              @click="deleteComment(comment.id)"
              style="color: #F56C6C; margin-left: 10px">
              删除
            </el-button>
          </div>
          <div class="comment-content">{{ comment.content }}</div>
          <el-button type="text" size="mini" @click="showReplyInput(comment)" v-if="isLoggedIn">回复</el-button>
        </div>

        <div class="replies" v-if="comment.replies && comment.replies.length > 0">
          <div class="reply-item" v-for="reply in comment.replies" :key="reply.id">
            <div class="reply-header">
              <span class="username">{{ reply.username }}</span>
              <span class="reply-to" v-if="reply.replyToUsername"> 回复 <span class="reply-to-user">{{ reply.replyToUsername }}</span></span>
              <span class="time">{{ formatTime(reply.createTime) }}</span>
              <el-button
                v-if="isLoggedIn && currentUsername === reply.username"
                type="text"
                size="mini"
                @click="deleteComment(reply.id)"
                style="color: #F56C6C; margin-left: 10px">
                删除
              </el-button>
            </div>
            <div class="reply-content">{{ reply.content }}</div>
            <el-button type="text" size="mini" @click="showReplyInput(comment, reply)" v-if="isLoggedIn">回复</el-button>
          </div>
        </div>

        <div class="reply-input" v-if="replyingTo && replyingTo.commentId === comment.id">
          <el-input
            type="textarea"
            :rows="2"
            :placeholder="replyingTo.replyToUsername ? '回复 ' + replyingTo.replyToUsername + '...' : '回复...'"
            v-model="replyContent">
          </el-input>
          <div style="margin-top: 5px">
            <el-button type="primary" size="mini" @click="submitReply" :loading="submitting">回复</el-button>
            <el-button size="mini" @click="cancelReply">取消</el-button>
          </div>
        </div>
      </div>
    </div>

    <el-pagination
      v-if="total > pageSize"
      background
      layout="total, prev, pager, next"
      :page-size="pageSize"
      :total="total"
      :current-page="currentPage"
      @current-change="handlePageChange"
      style="margin-top: 20px; text-align: center">
    </el-pagination>
  </div>
</template>

<script>
export default {
  name: 'CommentSection',
  props: {
    articleId: {
      type: Number,
      required: true
    }
  },
  data () {
    return {
      comments: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      newComment: '',
      replyContent: '',
      replyingTo: null,
      submitting: false,
      isLoggedIn: false,
      currentUsername: ''
    }
  },
  mounted () {
    this.checkLogin()
    this.loadComments()
  },
  methods: {
    checkLogin () {
      const username = localStorage.getItem('username') || sessionStorage.getItem('username')
      if (username) {
        this.isLoggedIn = true
        this.currentUsername = username
      }
    },
    loadComments () {
      this.$axios.get(`/article/${this.articleId}/comments/${this.pageSize}/${this.currentPage}`).then(resp => {
        if (resp && resp.data.code === 200) {
          this.comments = resp.data.result.content
          this.total = resp.data.result.totalElements
        }
      })
    },
    formatTime (time) {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleString('zh-CN')
    },
    submitComment () {
      if (!this.newComment.trim()) {
        this.$message.warning('请输入评论内容')
        return
      }
      this.submitting = true
      this.$axios.post('/article/comment', {
        articleId: this.articleId,
        content: this.newComment
      }).then(resp => {
        this.submitting = false
        if (resp && resp.data.code === 200) {
          this.$message.success('评论成功')
          this.newComment = ''
          this.loadComments()
          this.$emit('comment-added')
        } else {
          this.$message.error(resp.data.message || '评论失败')
        }
      }).catch(() => {
        this.submitting = false
        this.$message.error('评论失败')
      })
    },
    showReplyInput (comment, reply = null) {
      this.replyingTo = {
        commentId: comment.id,
        replyToUserId: reply ? reply.userId : comment.userId,
        replyToUsername: reply ? reply.username : comment.username
      }
      this.replyContent = ''
    },
    cancelReply () {
      this.replyingTo = null
      this.replyContent = ''
    },
    submitReply () {
      if (!this.replyContent.trim()) {
        this.$message.warning('请输入回复内容')
        return
      }
      this.submitting = true
      this.$axios.post('/article/comment', {
        articleId: this.articleId,
        content: this.replyContent,
        parentId: this.replyingTo.commentId,
        replyToUserId: this.replyingTo.replyToUserId,
        replyToUsername: this.replyingTo.replyToUsername
      }).then(resp => {
        this.submitting = false
        if (resp && resp.data.code === 200) {
          this.$message.success('回复成功')
          this.cancelReply()
          this.loadComments()
          this.$emit('comment-added')
        } else {
          this.$message.error(resp.data.message || '回复失败')
        }
      }).catch(() => {
        this.submitting = false
        this.$message.error('回复失败')
      })
    },
    deleteComment (id) {
      this.$confirm('确定要删除此评论吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.delete(`/article/comment/${id}`).then(resp => {
          if (resp && resp.data.code === 200) {
            this.$message.success('删除成功')
            this.loadComments()
            this.$emit('comment-added')
          } else {
            this.$message.error(resp.data.message || '删除失败')
          }
        })
      }).catch(() => {})
    },
    handlePageChange (page) {
      this.currentPage = page
      this.loadComments()
    }
  }
}
</script>

<style scoped>
.comment-section {
  margin-top: 30px;
}

.comment-input {
  margin-bottom: 20px;
}

.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid #EBEEF5;
}

.comment-main {
  margin-bottom: 10px;
}

.comment-header, .reply-header {
  margin-bottom: 8px;
}

.username {
  font-weight: bold;
  color: #409EFF;
  margin-right: 10px;
}

.time {
  color: #909399;
  font-size: 12px;
}

.comment-content, .reply-content {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 8px;
}

.replies {
  margin-left: 40px;
  padding-left: 15px;
  border-left: 2px solid #E4E7ED;
}

.reply-item {
  padding: 10px 0;
}

.reply-to {
  color: #909399;
  font-size: 13px;
}

.reply-to-user {
  color: #409EFF;
}

.reply-input {
  margin-left: 40px;
  margin-top: 10px;
  padding: 10px;
  background-color: #F5F7FA;
  border-radius: 4px;
}

.login-tip {
  margin-bottom: 20px;
}
</style>
