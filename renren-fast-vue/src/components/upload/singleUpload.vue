<template>
  <div>
    <el-upload
      action=""
      :http-request="upload"
      :limit="1"
      style="display: inline-block"
      :on-preview="handlePreview"
      :on-success="handleUploadSuccess"
      :on-remove="handleRemove"
      :file-list="fileList"
      list-type="picture">
      <el-button size="small" type="primary">点击上传</el-button>
      <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
    </el-upload>
    <el-dialog :visible.sync="dialogVisible">
      <img v-if="fileList.length > 0" width="100%" :src="fileList[0].url" alt="">
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'singleUpload',
  props: {
    value: String
  },
  data () {
    return {
      dialogVisible: false,
      url: this.value
    }
  },
  watch: {
    value (newVal) {
      this.url = newVal
    }
  },
  computed: {
    fileList () {
      return this.url ? [{
        name: this.url.substr(this.url.lastIndexOf('/') + 1),
        url: this.url
      }] : []
    }
  },
  methods: {
    upload (param) {
      const formData = new FormData()
      formData.append('file', param.file)
      this.$http({
        url: this.$http.adornUrl('/thirdParty/file/upload'),
        method: 'post',
        data: formData
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.url = data.data
          this.emitInput(this.url)
        } else {
          this.$message.error(data.msg)
        }
      })
    },
    emitInput (val) {
      this.$emit('input', val)
    },
    handleRemove () {
      this.emitInput('')
      this.url = ''
    },
    handlePreview () {
      this.dialogVisible = true
    },
    handleUploadSuccess () {
      console.log('上传成功...')
      this.emitInput(this.url)
    }
  }
}
</script>
