<template>
  <div class="publish-container">
    <el-card style="width: 990px; margin: 35px auto;">
      <div slot="header" class="clearfix">
        <span style="font-size: 18px; font-weight: bold;">发布笔记</span>
      </div>

      <!-- 标题输入 -->
      <div class="form-item">
        <el-input
          v-model="article.articleTitle"
          placeholder="请输入文章标题"
          maxlength="100"
          show-word-limit
          style="font-size: 16px;">
        </el-input>
      </div>

      <!-- 内容编辑器 -->
      <div class="form-item" style="height: 500px;">
        <mavon-editor
          v-model="article.articleContentMd"
          style="height: 100%;"
          ref="md"
          placeholder="开始编写你的笔记内容..."
          :toolbars="toolbars"
          fontSize="14px">
        </mavon-editor>
      </div>

      <!-- 摘要和封面 -->
      <div class="form-item">
        <el-collapse v-model="activeNames">
          <el-collapse-item title="设置摘要和封面（可选）" name="1">
            <div style="padding: 10px;">
              <el-form label-width="60px">
                <el-form-item label="摘要">
                  <el-input
                    type="textarea"
                    v-model="article.articleAbstract"
                    rows="4"
                    maxlength="255"
                    show-word-limit
                    placeholder="请输入文章摘要，不填写将自动提取正文前100字">
                  </el-input>
                </el-form-item>
                <el-form-item label="封面">
                  <el-input
                    v-model="article.articleCover"
                    placeholder="请输入封面图片URL">
                  </el-input>
                  <div style="margin-top: 10px; color: #909399; font-size: 12px;">
                    支持图片外链，留空将使用默认封面
                  </div>
                </el-form-item>
              </el-form>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>

      <!-- 操作按钮 -->
      <div class="form-item" style="text-align: center; margin-top: 20px;">
        <el-button @click="goBack">取 消</el-button>
        <el-button type="primary" @click="publishArticle" :loading="publishing">发 布</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'ArticlePublish',
  data() {
    return {
      article: {
        articleTitle: '',
        articleContentMd: '',
        articleContentHtml: '',
        articleAbstract: '',
        articleCover: ''
      },
      activeNames: [],
      publishing: false,
      toolbars: {
        bold: true,
        italic: true,
        header: true,
        underline: true,
        strikethrough: true,
        mark: true,
        superscript: true,
        subscript: true,
        quote: true,
        ol: true,
        ul: true,
        link: true,
        imagelink: true,
        code: true,
        table: true,
        fullscreen: true,
        readmodel: true,
        htmlcode: true,
        help: true,
        undo: true,
        redo: true,
        trash: true,
        save: false,
        navigation: true,
        alignleft: true,
        aligncenter: true,
        alignright: true,
        subfield: true,
        preview: true
      }
    }
  },
  mounted() {
    this.checkLogin()
  },
  methods: {
    checkLogin() {
      const username = localStorage.getItem('username')
      if (!username) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
      }
    },
    publishArticle() {
      // 验证标题
      if (!this.article.articleTitle || !this.article.articleTitle.trim()) {
        this.$message.warning('请输入文章标题')
        return
      }

      // 验证内容
      if (!this.article.articleContentMd || !this.article.articleContentMd.trim()) {
        this.$message.warning('请输入文章内容')
        return
      }

      // 自动生成摘要
      if (!this.article.articleAbstract || !this.article.articleAbstract.trim()) {
        // 移除markdown标记，提取纯文本
        const plainText = this.article.articleContentMd
          .replace(/[#*`\[\]!()]/g, '')
          .replace(/\n/g, ' ')
          .trim()
        this.article.articleAbstract = plainText.substring(0, 100) + (plainText.length > 100 ? '...' : '')
      }

      // 获取HTML内容
      this.article.articleContentHtml = this.$refs.md.d_render

      this.publishing = true

      // 发送请求
      this.$axios.post('/jotter/article', this.article).then(resp => {
        if (resp && resp.data.code === 200) {
          this.$message.success('发布成功')
          this.$router.push('/jotter')
        } else {
          this.$message.error(resp.data.message || '发布失败')
        }
      }).catch(err => {
        if (err.response && err.response.status === 401) {
          this.$message.error('请先登录')
          this.$router.push('/login')
        } else {
          this.$message.error('发布失败，请稍后重试')
        }
      }).finally(() => {
        this.publishing = false
      })
    },
    goBack() {
      this.$confirm('确定要取消发布吗？已填写的内容将丢失。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '继续编辑',
        type: 'warning'
      }).then(() => {
        this.$router.push('/jotter')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.publish-container {
  margin-top: 20px;
}

.form-item {
  margin-bottom: 20px;
}
</style>
