<template>
  <div class="comment-section">
    <el-card style="text-align: left; margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span style="font-size: 18px; font-weight: bold;">
          评论 ({{ totalComments }})
        </span>
      </div>

      <!-- 评论输入框 -->
      <div class="comment-input-area" v-if="isLoggedIn">
        <el-input
          type="textarea"
          :rows="3"
          placeholder="写下你的评论..."
          v-model="newCommentContent"
          maxlength="500"
          show-word-limit>
        </el-input>
        <div style="margin-top: 10px; text-align: right;">
          <el-button type="primary" size="small" @click="submitComment">发表评论</el-button>
        </div>
      </div>
      <div v-else class="login-tip">
        <el-alert
          title="请先登录后再发表评论"
          type="info"
          :closable="false"
          show-icon>
        </el-alert>
      </div>

      <!-- 评论列表 -->
      <div class="comment-list" style="margin-top: 20px;">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <!-- 一级评论 -->
          <div class="comment-main">
            <div class="comment-header">
              <span class="username">{{ comment.user ? comment.user.username : '未知用户' }}</span>
              <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-actions">
              <el-button type="text" size="mini" @click="showReplyInput(comment)">
                <i class="el-icon-chat-dot-round"></i> 回复
              </el-button>
              <el-button 
                v-if="canDelete(comment)" 
                type="text" 
                size="mini" 
                @click="deleteComment(comment.id)">
                <i class="el-icon-delete"></i> 删除
              </el-button>
            </div>
          </div>

          <!-- 回复输入框 -->
          <div v-if="replyTo === comment.id" class="reply-input-area" style="margin: 10px 0 10px 40px;">
            <el-input
              type="textarea"
              :rows="2"
              :placeholder="'回复 ' + (comment.user ? comment.user.username : '未知用户') + '...'"
              v-model="replyContent"
              maxlength="500"
              show-word-limit>
            </el-input>
            <div style="margin-top: 10px;">
              <el-button type="primary" size="mini" @click="submitReply(comment)">提交回复</el-button>
              <el-button size="mini" @click="cancelReply">取消</el-button>
            </div>
          </div>

          <!-- 二级回复列表 -->
          <div v-if="comment.replies && comment.replies.length > 0" class="reply-list">
            <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
              <div class="reply-header">
                <span class="username">{{ reply.user ? reply.user.username : '未知用户' }}</span>
                <span style="color: #909399; margin: 0 5px;">回复</span>
                <span class="reply-target">{{ reply.parentUserName || (comment.user ? comment.user.username : '未知用户') }}</span>
                <span class="comment-time">{{ formatDate(reply.createTime) }}</span>
              </div>
              <div class="reply-content">{{ reply.content }}</div>
              <div class="reply-actions">
                <el-button type="text" size="mini" @click="showReplyInput(comment, reply)">
                  <i class="el-icon-chat-dot-round"></i> 回复
                </el-button>
                <el-button 
                  v-if="canDelete(reply)" 
                  type="text" 
                  size="mini" 
                  @click="deleteComment(reply.id)">
                  <i class="el-icon-delete"></i> 删除
                </el-button>
              </div>
            </div>
          </div>

          <el-divider></el-divider>
        </div>

        <!-- 空状态 -->
        <el-empty v-if="comments.length === 0" description="暂无评论，快来发表第一条评论吧"></el-empty>
      </div>

      <!-- 分页 -->
      <el-pagination
        v-if="total > pageSize"
        background
        layout="prev, pager, next"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        @current-change="handlePageChange"
        style="margin-top: 20px; text-align: center;">
      </el-pagination>
    </el-card>
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
  data() {
    return {
      comments: [],
      total: 0,
      totalComments: 0,
      currentPage: 1,
      pageSize: 10,
      newCommentContent: '',
      replyContent: '',
      replyTo: null,
      replyToUser: null,
      currentUser: null,
      isLoggedIn: false
    }
  },
  mounted() {
    this.checkLoginStatus()
    this.loadComments()
    this.loadCommentCount()
  },
  methods: {
    checkLoginStatus() {
      const username = localStorage.getItem('username')
      this.isLoggedIn = !!username
      if (this.isLoggedIn) {
        this.currentUser = { username: username }
      }
    },
    loadComments() {
      this.$axios.get(`/comment/${this.articleId}/${this.pageSize}/${this.currentPage}`).then(resp => {
        if (resp && resp.data.code === 200) {
          this.comments = resp.data.result.content
          this.total = resp.data.result.totalElements
        }
      })
    },
    loadCommentCount() {
      this.$axios.get(`/comment/count/${this.articleId}`).then(resp => {
        if (resp && resp.data.code === 200) {
          this.totalComments = resp.data.result
        }
      })
    },
    submitComment() {
      if (!this.newCommentContent.trim()) {
        this.$message.warning('评论内容不能为空')
        return
      }

      const comment = {
        articleId: this.articleId,
        content: this.newCommentContent.trim(),
        parentId: null
      }

      this.$axios.post('/comment', comment).then(resp => {
        if (resp && resp.data.code === 200) {
          this.$message.success('评论发表成功')
          this.newCommentContent = ''
          this.currentPage = 1
          this.loadComments()
          this.loadCommentCount()
        } else {
          this.$message.error(resp.data.message || '评论发表失败')
        }
      }).catch(err => {
        if (err.response && err.response.status === 401) {
          this.$message.error('请先登录')
        } else {
          this.$message.error('评论发表失败')
        }
      })
    },
    showReplyInput(comment, replyToUser = null) {
      this.replyTo = comment.id
      this.replyToUser = replyToUser
      if (replyToUser) {
        this.replyContent = ''
      }
    },
    cancelReply() {
      this.replyTo = null
      this.replyToUser = null
      this.replyContent = ''
    },
    submitReply(parentComment) {
      if (!this.replyContent.trim()) {
        this.$message.warning('回复内容不能为空')
        return
      }

      const reply = {
        articleId: this.articleId,
        content: this.replyContent.trim(),
        parentId: parentComment.id
      }

      this.$axios.post('/comment', reply).then(resp => {
        if (resp && resp.data.code === 200) {
          this.$message.success('回复发表成功')
          this.cancelReply()
          this.loadComments()
          this.loadCommentCount()
        } else {
          this.$message.error(resp.data.message || '回复发表失败')
        }
      }).catch(err => {
        if (err.response && err.response.status === 401) {
          this.$message.error('请先登录')
        } else {
          this.$message.error('回复发表失败')
        }
      })
    },
    deleteComment(commentId) {
      this.$confirm('确定要删除这条评论吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.delete(`/comment/${commentId}`).then(resp => {
          if (resp && resp.data.code === 200) {
            this.$message.success('删除成功')
            this.loadComments()
            this.loadCommentCount()
          } else {
            this.$message.error(resp.data.message || '删除失败')
          }
        }).catch(err => {
          if (err.response && err.response.status === 401) {
            this.$message.error('请先登录')
          } else {
            this.$message.error('删除失败')
          }
        })
      }).catch(() => {})
    },
    canDelete(comment) {
      if (!this.isLoggedIn || !this.currentUser) {
        return false
      }
      return comment.user && comment.user.username === this.currentUser.username
    },
    handlePageChange(page) {
      this.currentPage = page
      this.loadComments()
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
  }
}
</script>

<style scoped>
.comment-section {
  width: 990px;
  margin: 0 auto;
}

.comment-input-area {
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.login-tip {
  padding: 20px;
}

.comment-item {
  margin-bottom: 15px;
}

.comment-main {
  padding: 10px 0;
}

.comment-header {
  margin-bottom: 8px;
}

.username {
  font-weight: bold;
  color: #409EFF;
  margin-right: 10px;
}

.comment-time {
  color: #909399;
  font-size: 12px;
}

.comment-content {
  color: #303133;
  line-height: 1.6;
  margin: 8px 0;
  word-wrap: break-word;
}

.comment-actions {
  margin-top: 5px;
}

.reply-list {
  margin-left: 40px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.reply-item {
  padding: 8px 0;
  border-bottom: 1px solid #ebeef5;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-header {
  margin-bottom: 5px;
}

.reply-target {
  color: #409EFF;
  font-weight: bold;
}

.reply-content {
  color: #606266;
  line-height: 1.5;
  margin: 5px 0;
  word-wrap: break-word;
}

.reply-actions {
  margin-top: 3px;
}

.reply-input-area {
  background-color: #fff;
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
}
</style>
