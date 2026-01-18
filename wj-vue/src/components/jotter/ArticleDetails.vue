<template>
  <div class="articles-area">
    <el-card style="text-align: left;width: 990px;margin: 35px auto 0 auto">
      <div>
        <span style="font-size: 20px"><strong>{{article.articleTitle}}</strong></span>
        <div style="font-size: 14px; color: #999; margin-top: 10px;">
          <span>作者：{{article.author ? article.author.nickname : '未知'}}</span>
        </div>
        <el-divider content-position="left">{{article.articleDate}}</el-divider>
        <div class="markdown-body">
          <div v-html="article.articleContentHtml"></div>
        </div>
        <el-divider v-if="canEditArticle"></el-divider>
        <div v-if="canEditArticle" style="text-align: right;">
          <el-button type="danger" size="small" @click="deleteArticle">删除文章</el-button>
        </div>
      </div>
    </el-card>

    <el-card style="text-align: left;width: 990px;margin: 20px auto 0 auto">
      <div slot="header">
        <span>评论区 ({{totalComments}})</span>
      </div>

      <div v-if="!this.$store.state.user.username">
        <el-alert type="info" title="请先登录后再评论" show-icon :closable="false" />
      </div>

      <div v-else>
        <el-input
          type="textarea"
          :rows="3"
          placeholder="说点什么..."
          v-model="newComment"
          style="margin-bottom: 10px;">
        </el-input>
        <el-button type="primary" @click="submitComment" style="float: right;">发表评论</el-button>
      </div>

      <el-divider style="clear: both;"></el-divider>

      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-header">
          <span class="comment-author">{{comment.user ? comment.user.nickname : '未知用户'}}</span>
          <span class="comment-time">{{comment.commentDate}}</span>
          <el-button
            v-if="canDeleteComment(comment)"
            type="text"
            size="mini"
            style="color: #f56c6c; float: right;"
            @click="handleDeleteComment(comment.id)">
            删除
          </el-button>
          <el-button
            v-if="this.$store.state.user.username"
            type="text"
            size="mini"
            style="color: #409EFF; float: right; margin-right: 10px;"
            @click="showReplyBox(comment)">
            回复
          </el-button>
        </div>
        <div class="comment-content">{{comment.content}}</div>

        <div v-if="comment.replyCount > 0" class="reply-section">
          <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
            <div class="reply-header">
              <span class="reply-author">{{reply.user ? reply.user.nickname : '未知用户'}}</span>
              <span v-if="reply.parentComment.user" class="reply-to"> 回复 {{reply.parentComment.user.nickname}}</span>
              <span class="reply-time">{{reply.commentDate}}</span>
              <el-button
                v-if="canDeleteComment(reply)"
                type="text"
                size="mini"
                style="color: #f56c6c; float: right;"
                @click="handleDeleteComment(reply.id)">
                删除
              </el-button>
              <el-button
                v-if="this.$store.state.user.username"
                type="text"
                size="mini"
                style="color: #409EFF; float: right; margin-right: 10px;"
                @click="showReplyBox(reply)">
                回复
              </el-button>
            </div>
            <div class="reply-content">{{reply.content}}</div>
          </div>
        </div>

        <div v-if="replyBoxId === comment.id" class="reply-box">
          <el-input
            type="textarea"
            :rows="2"
            placeholder="回复 {{comment.user ? comment.user.nickname : '该用户'}}..."
            v-model="replyContent"
            style="margin-bottom: 10px;">
          </el-input>
          <div style="text-align: right;">
            <el-button size="small" @click="cancelReply">取消</el-button>
            <el-button size="small" type="primary" @click="submitReply(comment)">发送</el-button>
          </div>
        </div>
      </div>

      <el-pagination
        v-if="totalPages > 1"
        background
        layout="total, prev, pager, next"
        @current-change="handleCommentPageChange"
        :page-size="commentPageSize"
        :total="totalComments"
        style="margin-top: 20px; justify-content: center;">
      </el-pagination>
    </el-card>
  </div>
</template>

<script>
  export default {
    name: 'ArticleDetails',
    data () {
      return {
        article: {},
        comments: [],
        newComment: '',
        replyContent: '',
        replyBoxId: null,
        commentPage: 0,
        commentPageSize: 10,
        totalComments: 0,
        totalPages: 0
      }
    },
    mounted () {
      this.loadArticle()
      this.loadComments()
    },
    computed: {
      canEditArticle () {
        return this.$store.state.user.username && 
               this.article.author && 
               this.article.author.username === this.$store.state.user.username
      }
    },
    methods: {
      loadArticle () {
        var _this = this
        this.$axios.get('/article/' + this.$route.query.id).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.article = resp.data.result
          }
        })
      },
      loadComments () {
        var _this = this
        this.$axios.get('/article/' + this.$route.query.id + '/comments', {
          params: {
            page: this.commentPage,
            size: this.commentPageSize
          }
        }).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.comments = resp.data.result.content
            _this.totalComments = resp.data.result.totalElements
            _this.totalPages = resp.data.result.totalPages
            _this.loadRepliesForComments()
          }
        })
      },
      loadRepliesForComments () {
        this.comments.forEach(comment => {
          if (comment.replyCount > 0) {
            this.loadReplies(comment)
          }
        })
      },
      loadReplies (comment) {
        var _this = this
        this.$axios.get('/comment/' + comment.id + '/replies').then(resp => {
          if (resp && resp.data.code === 200) {
            comment.replies = resp.data.result
          }
        })
      },
      submitComment () {
        if (!this.newComment.trim()) {
          this.$message.warning('评论内容不能为空')
          return
        }
        var _this = this
        this.$axios.post('/article/' + this.$route.query.id + '/comment', {
          content: this.newComment
        }).then(resp => {
          if (resp && resp.data.code === 200) {
            this.$message.success('评论成功')
            this.newComment = ''
            this.loadComments()
            this.loadArticle()
          } else {
            this.$message.error(resp.data.msg || '评论失败')
          }
        }).catch(() => {
          this.$message.error('评论失败')
        })
      },
      showReplyBox (comment) {
        this.replyBoxId = comment.id
        this.replyContent = ''
      },
      cancelReply () {
        this.replyBoxId = null
        this.replyContent = ''
      },
      submitReply (comment) {
        if (!this.replyContent.trim()) {
          this.$message.warning('回复内容不能为空')
          return
        }
        var _this = this
        this.$axios.post('/article/' + this.$route.query.id + '/comment', {
          content: this.replyContent,
          parentCommentId: comment.id
        }).then(resp => {
          if (resp && resp.data.code === 200) {
            this.$message.success('回复成功')
            this.cancelReply()
            this.loadComments()
          } else {
            this.$message.error(resp.data.msg || '回复失败')
          }
        }).catch(() => {
          this.$message.error('回复失败')
        })
      },
      handleDeleteComment (commentId) {
        this.$confirm('确定要删除这条评论吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          var _this = this
          this.$axios.delete('/comment/' + commentId).then(resp => {
            if (resp && resp.data.code === 200) {
              this.$message.success('删除成功')
              this.loadComments()
              this.loadArticle()
            } else {
              this.$message.error(resp.data.msg || '删除失败')
            }
          }).catch(() => {
            this.$message.error('删除失败')
          })
        }).catch(() => {})
      },
      deleteArticle () {
        this.$confirm('确定要删除这篇文章吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          var _this = this
          this.$axios.delete('/article/' + this.$route.query.id).then(resp => {
            if (resp && resp.data.code === 200) {
              this.$message.success('删除成功')
              this.$router.push('/jotter/articles')
            } else {
              this.$message.error(resp.data.msg || '删除失败')
            }
          }).catch(() => {
            this.$message.error('删除失败')
          })
        }).catch(() => {})
      },
      canDeleteComment (comment) {
        const currentUser = this.$store.state.user
        if (!currentUser || !currentUser.username) return false
        if (comment.user && comment.user.username === currentUser.username) return true
        if (this.article.author && this.article.author.username === currentUser.username) return true
        return false
      },
      handleCommentPageChange (page) {
        this.commentPage = page - 1
        this.loadComments()
      }
    }
  }
</script>

<style scoped>
  @import "../../styles/markdown.css";

  .comment-item {
    margin-bottom: 20px;
    padding-bottom: 20px;
    border-bottom: 1px solid #eee;
  }

  .comment-item:last-child {
    border-bottom: none;
  }

  .comment-header {
    margin-bottom: 10px;
  }

  .comment-author {
    font-weight: bold;
    color: #333;
  }

  .comment-time {
    font-size: 12px;
    color: #999;
    margin-left: 15px;
  }

  .comment-content {
    font-size: 14px;
    line-height: 1.6;
    color: #333;
    white-space: pre-wrap;
  }

  .reply-section {
    margin-top: 15px;
    margin-left: 40px;
    padding-left: 20px;
    border-left: 2px solid #e4e7ed;
  }

  .reply-item {
    margin-bottom: 15px;
    padding-bottom: 15px;
    border-bottom: 1px dashed #f0f0f0;
  }

  .reply-item:last-child {
    border-bottom: none;
  }

  .reply-header {
    margin-bottom: 8px;
  }

  .reply-author {
    font-weight: bold;
    color: #409EFF;
    font-size: 13px;
  }

  .reply-to {
    color: #909399;
    font-size: 12px;
  }

  .reply-time {
    font-size: 12px;
    color: #c0c4cc;
    margin-left: 10px;
  }

  .reply-content {
    font-size: 13px;
    line-height: 1.5;
    color: #606266;
    white-space: pre-wrap;
  }

  .reply-box {
    margin-top: 15px;
    padding: 15px;
    background-color: #f5f7fa;
    border-radius: 4px;
  }
</style>
