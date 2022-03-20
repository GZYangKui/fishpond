/**
 *
 * 验证邮箱地址是否正确
 *
 */
export function checkEmailFormat(value){
    if (!value){
        return false;
    }
    let reg = /^([a-zA-Z]|\d)(\w|-)+@[a-zA-Z\d]+\.([a-zA-Z]{2,4})$/;
    return reg.test(value);
}