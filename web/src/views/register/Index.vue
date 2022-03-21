<template>
  <el-row justify="center" align="middle" style="height: 100%">
    <el-form label-width="100px" :model="registerInfo" :rules="rules" ref="regFrom">
      <el-form-item label="邮箱地址:" prop="email">
        <el-input type="email" v-model="registerInfo.email"/>
      </el-form-item>
      <el-form-item label="验证码:" prop="code">
        <el-input v-model="registerInfo.code">
          <template #append>
            <el-button @click="handleGetCode" :disabled="btnSts">{{ btnText }}</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="登录密码:" prop="pw">
        <el-input type="password" v-model="registerInfo.pw"/>
      </el-form-item>
      <el-form-item>
        <el-row justify="end" style="width: 100%">
          <el-button @click="submit">立即注册</el-button>
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
import md5 from 'js-md5';
import {code} from "../../api/Kapt";
import {VCode, register} from "../../api/User";
import {checkEmailFormat} from "../../util/validator";

const defaultRegisterInfo = {
  pw: null,
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
    const checkEmail = (rule, value, callback) => {
      if (checkEmailFormat(value)) {
        callback()
      } else {
        callback(new Error("请输入正确邮箱地址!"))
      }
    }
    return {
      btnSts: false,
      btnText: "获取",
      base64Str: null,
      dialogVisible: false,
      VCode: Object.assign({}, defaultVCode),
      registerInfo: Object.assign({}, defaultRegisterInfo),
      rules: {
        "email": [
          {
            required: true, trigger: 'blur', validator: checkEmail
          }
        ],
        "code": [
          {
            required: true, trigger: 'blur', message: "验证码不能为空!"
          }
        ],
        "pw": [
          {
            required: true, trigger: 'blur', message: "密码不能为空"
          }
        ]
      }
    }
  },
  methods: {
    handleGetCode() {
      if (!checkEmailFormat(this.registerInfo.email)) {
        this.$message.warning("请填写正确邮箱地址!")
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
          this.btnText = "获取";
        } else {
          this.btnText = time.toString();
        }
      }, 1000)
    },
    submit() {
      this.$refs.regFrom.validate((valid) => {
        if (!valid) {
          return;
        }
        let info = Object.assign({}, defaultRegisterInfo)
        info.code = this.registerInfo.code;
        info.email = this.registerInfo.email;
        //md5加密
        info.pw = md5(this.registerInfo.pw).toUpperCase();

        //用户注册
        register(info).then(resp => {
          this.$message.success("注册成功!");
          this.registerInfo = Object.assign({}, defaultRegisterInfo);
        });
      })
    }
  }
}
</script>

<style scoped>
.el-input {
  width: 300px;
}
</style>