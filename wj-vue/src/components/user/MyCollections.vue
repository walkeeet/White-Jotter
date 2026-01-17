<template>
  <div class="my-collections">
    <el-row style="margin: 18px 0px 0px 18px ">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
        <el-breadcrumb-item>个人中心</el-breadcrumb-item>
        <el-breadcrumb-item>我的收藏</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>

    <el-card style="margin: 18px 2%;width: 95%">
      <div slot="header">
        <span>我的收藏书籍</span>
      </div>

      <div v-if="collections.length === 0" class="empty-state">
        <el-empty description="暂无收藏的书籍">
          <el-button type="primary" @click="$router.push('/library')">去图书馆看看</el-button>
        </el-empty>
      </div>

      <div v-else class="collections-grid">
        <el-tooltip
          v-for="item in collections"
          :key="item.id"
          effect="dark"
          placement="top">
          <div slot="content">
            <p style="font-size: 14px;margin-bottom: 6px;">{{item.book.title}}</p>
            <p style="font-size: 13px;margin-bottom: 6px">
              <span>{{item.book.author}}</span> /
              <span>{{item.book.date}}</span> /
              <span>{{item.book.press}}</span>
            </p>
            <p style="width: 300px" class="abstract">{{item.book.abs}}</p>
            <p style="font-size: 12px;color: #999">
              收藏时间: {{ formatDate(item.collectionTime) }}
            </p>
          </div>

          <el-card
            class="collection-card"
            shadow="hover"
            body-style="padding: 10px">
            <div class="book-cover">
              <img :src="item.book.cover" alt="封面">
            </div>
            <div class="book-info">
              <div class="book-title">{{item.book.title}}</div>
              <div class="book-author">{{item.book.author}}</div>
              <el-button
                type="danger"
                size="mini"
                @click="removeCollection(item.book.id)"
                :loading="loading[item.book.id]">
                取消收藏
              </el-button>
            </div>
          </el-card>
        </el-tooltip>
      </div>
    </el-card>
  </div>
</template>

<script>
  export default {
    name: 'MyCollections',
    data () {
      return {
        collections: [],
        loading: {}
      }
    },
    mounted () {
      this.loadCollections()
    },
    methods: {
      loadCollections () {
        var _this = this
        this.$axios.get('/api/collection/my').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.collections = resp.data.result
          }
        }).catch(() => {
          this.$message.error('加载收藏列表失败')
        })
      },
      removeCollection (bookId) {
        this.$confirm('确定要取消收藏这本书吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.loading[bookId] = true
          this.$axios.post('/api/collection/remove', { bookId: bookId }).then(resp => {
            if (resp && resp.data.code === 200) {
              this.$message.success('已取消收藏')
              this.collections = this.collections.filter(item => item.book.id !== bookId)
            } else {
              this.$message.error(resp.data.message)
            }
          }).catch(() => {
            this.$message.error('操作失败')
          }).finally(() => {
            this.loading[bookId] = false
          })
        }).catch(() => {
          // 用户取消
        })
      },
      formatDate (dateStr) {
        if (!dateStr) return ''
        const date = new Date(dateStr)
        return date.toLocaleString('zh-CN')
      }
    }
  }
</script>

<style scoped>
  .empty-state {
    padding: 40px 0;
  }

  .collections-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
  }

  .collection-card {
    width: 280px;
    margin-bottom: 20px;
  }

  .book-cover {
    float: left;
    margin-right: 15px;
  }

  .book-cover img {
    width: 100px;
    height: 145px;
    object-fit: cover;
  }

  .book-info {
    overflow: hidden;
  }

  .book-title {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 8px;
    color: #333;
  }

  .book-author {
    font-size: 14px;
    color: #666;
    margin-bottom: 10px;
  }

  .abstract {
    display: block;
    line-height: 17px;
  }
</style>
