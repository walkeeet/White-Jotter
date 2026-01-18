<template>
  <div class="jotter-editor-container">
    <el-card class="editor-card">
      <div slot="header">
        <span>发布笔记</span>
      </div>

      <el-form :model="article" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="article.articleTitle" placeholder="请输入文章标题" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="摘要">
          <el-input
            type="textarea"
            v-model="article.articleAbstract"
            placeholder="请输入文章摘要（可选）"
            :rows="3"
            maxlength="255"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="封面">
          <el-input v-model="article.articleCover" placeholder="图片 URL" style="margin-bottom: 10px;" />
          <div v-if="article.articleCover">
            <el-image :src="article.articleCover" style="width: 200px; height: 150px;" fit="cover" />
          </div>
        </el-form-item>

        <el-form-item label="内容">
          <mavon-editor
            v-model="article.articleContentMd"
            style="height: 500px;"
            ref="mdEditor"
            @save="saveArticle"
            @imgAdd="handleImgAdd"
            fontSize="16px"
          >
          </mavon-editor>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="saveArticle('publish')">发布</el-button>
          <el-button @click="cancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
  export default {
    name: 'JotterEditor',
    data () {
      return {
        article: {
          id: null,
          articleTitle: '',
          articleAbstract: '',
          articleContentMd: '',
          articleContentHtml: '',
          articleCover: ''
        }
      }
    },
    mounted () {
      if (this.$route.query.id) {
        this.loadArticle(this.$route.query.id)
      }
    },
    methods: {
      loadArticle (id) {
        var _this = this
        this.$axios.get('/article/' + id).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.article = resp.data.result
          }
        })
      },
      saveArticle (type) {
        const _this = this

        if (!this.article.articleTitle.trim()) {
          this.$message.warning('请输入文章标题')
          return
        }

        if (!this.article.articleContentMd.trim()) {
          this.$message.warning('请输入文章内容')
          return
        }

        const render = this.$refs.mdEditor.d_render
        this.article.articleContentHtml = render

        this.$confirm('确认发布这篇笔记吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          const apiUrl = _this.article.id ? '/article/' + _this.article.id : '/article'
          const method = _this.article.id ? 'put' : 'post'

          _this.$axios[method](apiUrl, {
            id: _this.article.id,
            articleTitle: _this.article.articleTitle,
            articleContentMd: _this.article.articleContentMd,
            articleContentHtml: _this.article.articleContentHtml,
            articleAbstract: _this.article.articleAbstract || _this.article.articleContentMd.substring(0, 200),
            articleCover: _this.article.articleCover
          }).then(resp => {
            if (resp && resp.data.code === 200) {
              _this.$message.success('发布成功')
              _this.$router.push({
                path: '/jotter/article',
                query: { id: resp.data.result ? resp.data.result.id : _this.article.id }
              })
            } else {
              _this.$message.error(resp.data.msg || '发布失败')
            }
          }).catch(() => {
            _this.$message.error('发布失败，请稍后重试')
          })
        }).catch(() => {
          _this.$message({ type: 'info', message: '已取消发布' })
        })
      },
      handleImgAdd (pos, $file) {
        var _this = this
        var formdata = new FormData()
        formdata.append('file', $file)
        this.$axios.post('/admin/content/books/covers', formdata, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }).then(resp => {
          const url = resp.data
          _this.$refs.mdEditor.$img2Url(pos, url)
        }).catch(() => {
          this.$message.error('图片上传失败')
        })
      },
      cancel () {
        this.$router.go(-1)
      }
    }
  }
</script>

<style scoped>
  .jotter-editor-container {
    padding: 20px;
    display: flex;
    justify-content: center;
  }

  .editor-card {
    width: 100%;
    max-width: 1200px;
  }
</style>