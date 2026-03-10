<template>
  <div>
    <el-row style="margin: 18px 0px 0px 18px ">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
        <el-breadcrumb-item>我的收藏</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <el-card style="margin: 18px 2%;width: 95%">
      <el-row v-if="collections.length > 0" style="height: auto;min-height: 600px;">
        <el-tooltip effect="dark" placement="right"
                    v-for="item in collections.slice((currentPage-1)*pagesize,currentPage*pagesize)"
                    :key="item.id">
          <p slot="content" style="font-size: 14px;margin-bottom: 6px;">{{item.book.title}}</p>
          <p slot="content" style="font-size: 13px;margin-bottom: 6px">
            <span>{{item.book.author}}</span> /
            <span>{{item.book.date}}</span> /
            <span>{{item.book.press}}</span>
          </p>
          <p slot="content" style="width: 300px" class="abstract">{{item.book.abs}}</p>
          <el-card style="width: 135px;margin-bottom: 20px;height: 250px;float: left;margin-right: 15px" class="book"
                   bodyStyle="padding:10px" shadow="hover">
            <div class="cover">
              <img :src="item.book.cover" alt="封面">
            </div>
            <div class="info">
              <div class="title">
                <a href="">{{item.book.title}}</a>
              </div>
            </div>
            <div class="author">{{item.book.author}}</div>
            <div class="collect-time">{{ formatDate(item.collectTime) }}</div>
            <el-button class="remove-btn" type="danger" size="mini" @click.stop="removeCollection(item)">
              <i class="el-icon-delete"></i>
              取消收藏
            </el-button>
          </el-card>
        </el-tooltip>
      </el-row>
      <el-row v-else style="text-align: center;padding: 100px 0;">
        <el-empty description="暂无收藏书籍"></el-empty>
      </el-row>
      <el-row v-if="collections.length > 0">
        <el-pagination
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-size="pagesize"
          :total="collections.length">
        </el-pagination>
      </el-row>
    </el-card>
  </div>
</template>

<script>
  export default {
    name: 'BookCollection',
    data () {
      return {
        collections: [],
        currentPage: 1,
        pagesize: 18
      }
    },
    mounted: function () {
      this.loadCollections()
    },
    methods: {
      loadCollections () {
        var _this = this
        this.$axios.get('/collection/list').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.collections = resp.data.result
          }
        })
      },
      removeCollection (item) {
        var _this = this
        this.$confirm('确定要取消收藏这本书吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          _this.$axios.post('/collection/remove', null, {
            params: { bookId: item.bookId }
          }).then(resp => {
            if (resp && resp.data.code === 200) {
              _this.$message.success('取消收藏成功')
              _this.loadCollections()
            } else {
              _this.$message.error(resp.data.message)
            }
          })
        }).catch(() => {
        })
      },
      handleCurrentChange: function (currentPage) {
        this.currentPage = currentPage
      },
      formatDate (date) {
        if (!date) return ''
        const d = new Date(date)
        const year = d.getFullYear()
        const month = (d.getMonth() + 1).toString().padStart(2, '0')
        const day = d.getDate().toString().padStart(2, '0')
        return `${year}-${month}-${day}`
      }
    }
  }
</script>

<style scoped>
  .cover {
    width: 115px;
    height: 172px;
    margin-bottom: 7px;
    overflow: hidden;
    cursor: pointer;
  }

  img {
    width: 115px;
    height: 172px;
  }

  .title {
    font-size: 14px;
    text-align: left;
  }

  .author {
    color: #333;
    width: 102px;
    font-size: 13px;
    margin-bottom: 4px;
    text-align: left;
  }

  .collect-time {
    color: #999;
    font-size: 12px;
    text-align: left;
    margin-bottom: 4px;
  }

  .abstract {
    display: block;
    line-height: 17px;
  }

  .remove-btn {
    position: absolute;
    bottom: 10px;
    right: 10px;
    width: 70px;
    padding: 4px 0;
  }

  .book {
    position: relative;
  }

  a {
    text-decoration: none;
  }

  a:link, a:visited, a:focus {
    color: #3377aa;
  }
</style>
