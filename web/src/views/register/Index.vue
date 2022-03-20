<template>
  <el-row justify="center" align="middle" style="height: 100%">
    <el-form label-width="100px">
      <el-form-item label="邮箱地址:">
        <el-input type="email" v-model="registerInfo.email"/>
      </el-form-item>
      <el-form-item label="验证码:">
        <el-input v-model="registerInfo.code">
          <template #append>
            <el-button @click="handleGetCode" :disabled="btnSts">{{ btnText }}</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-row justify="end" style="width: 100%">
          <el-button>立即注册</el-button>
        </el-row>
      </el-form-item>
    </el-form>
    <el-dialog v-model="dialogVisible" title="发送验证码" width="30%">
      <el-form label-width="100px">
        <el-form-item>
          <div style="width: 200px;height: 50px">
            <el-image :src="base64Str" @click="handleGetCode"/>
          </div>
        </el-form-item>
        <el-form-item>
          <el-input v-model="VCode.code"/>
        </el-form-item>
        <el-form-item>
          <el-button @click="handleVCode">发送</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </el-row>
</template>

<script>
import {code} from "../../api/Kapt";
import {VCode} from "../../api/User";

const defaultRegisterInfo = {
  email: null,
  code: null
};
const defaultVCode = {
  uuid: null,
  code: null,
  email: null
}
export default {
  name: "Index",
  data() {
    return {
      btnSts: false,
      btnText: "发送",
      base64Str: null,
      dialogVisible: false,
      VCode: Object.assign({}, defaultVCode),
      registerInfo: Object.assign({}, defaultRegisterInfo)
    }
  },
  methods: {
    handleGetCode() {
      if (this.btnSts) {
        return;
      }
      let email = this.registerInfo.email
      if (!email || !email.trim()) {
        this.$message.warning("请输入邮箱地址后在获取验证码!");
        return
      }
      this.VCode.email = email
      this.dialogVisible = true;
      code().then(resp => {
        let data = resp.data;
        this.VCode.uuid = data.uuid;
        this.base64Str = `data:image/png;base64,${data.img}`
      })
    },
    handleVCode() {
      VCode(this.VCode).then(resp => {
        this.dialogVisible = false;
        this.$message.success("发送成功!");
        //开启定时器
        this.handleSTimer();
      })
    },
    handleSTimer() {
      //禁用按钮
      this.btnSts = true;

      let time = 180;
      let id = setInterval(() => {
        time--;
        if (time <= 0) {
          clearInterval(id);
          this.btnSts = false;
          this.btnText = "发送";
        } else {
          this.btnText = time.toString();
        }
      }, 1000)
    }
  }
}
</script>

<style scoped>
.el-input {
  width: 300px;
}
</style>