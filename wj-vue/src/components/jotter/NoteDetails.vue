<template>
  <div class="note-detail">
    <div class="note-area">
      <el-card style="text-align: left;width: 990px;margin: 35px auto 0 auto">
        <div>
          <span style="font-size: 20px"><strong>{{note.title}}</strong></span>
          <el-divider content-position="left">
            <span>{{note.createTime}}</span>
            <el-button v-if="isOwner" type="danger" size="mini" style="float:right" @click="handleDelete">删除</el-button>
          </el-divider>
          <div class="markdown-body">
            <div v-html="note.contentHtml"></div>
          </div>
        </div>
      </el-card>

      <div class="comment-area">
        <el-card style="text-align: left;width: 990px;margin: 20px auto 0 auto">
          <div class="comment-header">
            <h3>评论 ({{total}})</h3>
          </div>
          <div v-if="isLogin" class="comment-input">
            <el-input
              type="textarea"
              :rows="3"
              v-model="newComment"
              placeholder="写下你的评论..."
              maxlength="500">
            </el-input>
            <el-button type="primary" style="margin-top: 10px" @click="submitComment">发布评论</el-button>
          </div>
          <div v-else class="login-tip">
            <span>请先 <router-link to="/login">登录</router-link> 后再评论</span>
          </div>

          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <el-divider></el-divider>
            <div class="comment-content">
              <div class="comment-meta">
                <strong>{{comment.user ? comment.user.username : '匿名用户'}}</strong>
                <span class="comment-time">{{comment.createTime}}</span>
                <el-button
                  v-if="isLogin && currentUserId === comment.userId"
                  type="text"
                  size="mini"
                  @click="deleteComment(comment.id)">删除</el-button>
              </div>
              <p>{{comment.content}}</p>
              <div class="comment-actions">
                <el-button type="text" size="mini" @click="showReplyInput(comment)">回复</el-button>
              </div>

              <div v-if="replyingTo === comment.id" class="reply-input">
                <el-input
                  v-model="replyContent"
                  placeholder="回复评论..."
                  maxlength="500">
                </el-input>
                <el-button type="primary" size="small" @click="submitReply(comment)">回复</el-button>
                <el-button size="small" @click="cancelReply">取消</el-button>
              </div>

              <div v-for="child in comment.children" :key="child.id" class="reply-item">
                <div class="reply-content">
                  <span class="reply-user">{{child.user ? child.user.username : '匿名用户'}}</span>
                  <span v-if="child.replyUser"> 回复 </span>
                  <span v-if="child.replyUser" class="reply-to-user">{{child.replyUser ? child.replyUser.username : ''}}</span>
                  <span class="reply-time">{{child.createTime}}</span>
                  <p>{{child.content}}</p>
                  <el-button
                    v-if="isLogin && currentUserId === child.userId"
                    type="text"
                    size="mini"
                    @click="deleteComment(child.id)">删除</el-button>
                </div>
              </div>
            </div>
          </div>

          <el-pagination
            v-if="total > pageSize"
            background
            layout="prev, pager, next"
            @current-change="handleCommentPageChange"
            :page-size="pageSize"
            :total="total"
            :current-page="commentPage">
          </el-pagination>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'NoteDetails',
    data () {
      return {
        note: {},
        comments: [],
        newComment: '',
        replyContent: '',
        replyingTo: null,
        isLogin: false,
        currentUserId: null,
        isOwner: false,
        total: 0,
        pageSize: 10,
        commentPage: 1
      }
    },
    mounted () {
      this.checkLogin()
      this.loadNote()
      this.loadComments()
    },
    methods: {
      checkLogin () {
      this.isLogin = !!this.$store.state.user.id
      this.currentUserId = this.$store.state.user.id
    },
      loadNote () {
      var _this = this
      this.$axios.get('/note/' + this.$route.query.id).then(resp => {
        if (resp && resp.data.code === 200) {
          _this.note = resp.data.result
          _this.isOwner = _this.isLogin && _this.note.userId === _this.currentUserId
        }
      })
    },
      loadComments () {
      var _this = this
      this.$axios.get('/comment/' + this.$route.query.id + '/' + this.pageSize + '/' + this.commentPage).then(resp => {
        if (resp && resp.data.code === 200) {
          _this.comments = resp.data.result.content
          _this.total = resp.data.result.totalElements
        }
      })
    },
      handleDelete () {
      var _this = this
      this.$confirm('确定要删除这条笔记吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        _this.$axios.delete('/note/' + _this.$route.query.id).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.$message.success('删除成功')
            _this.$router.push({ path: '/jotter' })
          } else {
            _this.$message.error(resp.data.message)
          }
        })
      })
    },
      submitComment () {
      var _this = this
      if (this.newComment.trim()) {
        this.$axios.post('/comment', {
          articleId: parseInt(this.$route.query.id),
          content: this.newComment
        }).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.$message.success('评论成功')
            _this.newComment = ''
            _this.loadComments()
          } else {
            _this.$message.error(resp.data.message)
          }
        })
      }
    },
      showReplyInput (comment) {
        this.replyingTo = comment.id
        this.replyContent = ''
      },
      cancelReply () {
        this.replyingTo = null
        this.replyContent = ''
      },
      submitReply (parentComment) {
        var _this = this
        if (this.replyContent.trim()) {
          this.$axios.post('/comment', {
          articleId: parseInt(this.$route.query.id),
          parentId: parentComment.id,
          replyUserId: parentComment.userId,
          content: this.replyContent
        }).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.$message.success('回复成功')
            _this.cancelReply()
            _this.loadComments()
          } else {
            _this.$message.error(resp.data.message)
          }
        })
      }
    },
      deleteComment (id) {
      var _this = this
      this.$confirm('确定要删除这条评论吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        _this.$axios.delete('/comment/' + id).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.$message.success('删除成功')
            _this.loadComments()
          } else {
            _this.$message.error(resp.data.message)
          }
        })
      })
    },
      handleCommentPageChange (page) {
      this.commentPage = page
      this.loadComments()
    }
  }
}
</script>

<style scoped>
  @import "../../styles/markdown.css";

  .note-area {
    width: 990px;
    margin: 0 auto;
  }

  .comment-area {
    margin-bottom: 35px;
  }

  .comment-header h3 {
    margin: 0;
  }

  .comment-input {
    margin: 20px 0;
  }

  .login-tip {
    margin: 20px 0;
    color: #909399;
  }

  .comment-item {
    margin: 20px 0;
  }

  .comment-meta {
    color: #606266;
    margin-bottom: 10px;
  }

  .comment-time {
    margin-left: 10px;
    color: #909399;
    font-size: 12px;
  }

  .comment-actions {
    margin-top: 10px 0;
  }

  .reply-input {
    margin: 10px 0 10px 20px;
  }

  .reply-item {
    margin: 10px 0 10px 20px;
    padding: 10px;
    background: #f5f7fa;
    border-radius: 4px;
  }

  .reply-content {
    color: #606266;
  }

  .reply-user {
    font-weight: bold;
  }

  .reply-to-user {
    color: #409EFF;
  }

  .reply-time {
    margin-left: 10px;
    color: #909399;
    font-size: 12px;
  }
</style>
