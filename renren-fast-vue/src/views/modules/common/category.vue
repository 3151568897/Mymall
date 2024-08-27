<template>
  <el-tree :data="menus"
           :props="defaultProps"
           node-key="catId"
           @node-click="nodeClick"
           ref="menuTree">
  </el-tree>
</template>

<script>
export default {
  data () {
    return {
      menus: [],
      expandedKeys: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      }
    }
  },
  props: {
    catId: {
      type: Number,
      default: 0
    }
  },
  methods: {
    getMenus () {
      this.$http({
        url: this.$http.adornUrl('/product/category/list/tree'),
        method: 'get'
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.menus = data.data
        } else {
          this.menus = []
        }
      }).catch((error) => {
        console.log(error)
      })
    },
    nodeClick (data, node, component) {
      this.$emit('tree-node-click', data, node, component)
    }
  },
  created () {
    this.getMenus()
  }
}
</script>

<style scoped lang="scss">

</style>
