<template>
  <div>
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>我的收藏</span>
      </div>
      <el-row v-loading="loading">
        <el-col
          v-for="item in favorites"
          :key="item.id"
          :span="4"
          style="padding: 10px;">
          <el-card
            shadow="hover"
            :body-style="{ padding: '10px' }"
            style="height: 260px;">
            <div class="cover">
              <img :src="item.cover" alt="封面" style="width: 100%; height: 172px; object-fit: cover;">
            </div>
            <div class="info" style="margin-top: 10px;">
              <div class="title" style="font-size: 14px; font-weight: bold; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                {{ item.title }}
              </div>
              <div class="author" style="font-size: 12px; color: #999; margin-top: 5px;">
                {{ item.author }}
              </div>
            </div>
            <el-button
              type="danger"
              size="mini"
              style="width: 100%; margin-top: 10px;"
              @click="removeFavorite(item)">
              取消收藏
            </el-button>
          </el-card>
        </el-col>
        <el-col :span="24" v-if="favorites.length === 0 && !loading">
          <div style="text-align: center; padding: 40px; color: #999;">
            暂无收藏的书籍
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'UserFavorites',
  data () {
    return {
      favorites: [],
      loading: false
    }
  },
  mounted () {
    this.loadFavorites()
  },
  watch: {
    '$route' (to, from) {
      if (to.path === '/admin/favorites') {
        this.loadFavorites()
      }
    }
  },
  methods: {
    loadFavorites () {
      var _this = this
      _this.loading = true
      this.$axios.get('/favorites').then(resp => {
        if (resp && resp.data.code === 200) {
          _this.favorites = resp.data.result
        } else {
          _this.$message.error(resp.data.message || '加载失败')
        }
        _this.loading = false
      }).catch(() => {
        _this.loading = false
        _this.$message.error('加载失败')
      })
    },
    removeFavorite (item) {
      var _this = this
      this.$confirm('确定取消收藏这本书吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        _this.$axios.post('/favorites/remove', {id: item.id}).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.$message.success('取消收藏成功')
            _this.loadFavorites()
          } else {
            _this.$message.error(resp.data.message || '操作失败')
          }
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.box-card {
  margin: 20px;
}

.cover {
  width: 100%;
  height: 172px;
  overflow: hidden;
}
</style>
