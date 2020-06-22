var encryptAes = function(s, pass) {
  var parsedBase64Key = CryptoJS.enc.Base64.parse(pass);
  return CryptoJS.AES.encrypt(plaintText, parsedBase64Key, {
mode: CryptoJS.mode.ECB,
padding: CryptoJS.pad.Pkcs7
});
console.log( "encryptedData = " + encryptedData );
}
