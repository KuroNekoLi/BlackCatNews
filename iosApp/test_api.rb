#!/usr/bin/env ruby

require 'jwt'
require 'net/http'
require 'json'
require 'time'

# 設定
ASC_KEY_ID = "3SFDZ34ZZ6"
ASC_ISSUER_ID = "69a6de82-8b47-47e3-e053-5b8c7c11a4d1"
ASC_PRIVATE_KEY_PATH = "/Users/linli/Downloads/AuthKey_3SFDZ34ZZ6.p8"

begin
  # 讀取私鑰
  private_key = File.read(ASC_PRIVATE_KEY_PATH)
  
  # 產生 JWT
  payload = {
    iss: ASC_ISSUER_ID,
    exp: Time.now.to_i + 20 * 60,  # 20 分鐘後過期
    aud: 'appstoreconnect-v1'
  }
  
  header = {
    alg: 'ES256',
    kid: ASC_KEY_ID,
    typ: 'JWT'
  }
  
  jwt_token = JWT.encode(payload, OpenSSL::PKey::EC.new(private_key), 'ES256', header)
  puts "✅ JWT Token 已產生，長度: #{jwt_token.length}"
  
  # 呼叫 App Store Connect API
  uri = URI('https://api.appstoreconnect.apple.com/v1/apps')
  http = Net::HTTP.new(uri.host, uri.port)
  http.use_ssl = true
  
  request = Net::HTTP::Get.new(uri)
  request['Authorization'] = "Bearer #{jwt_token}"
  request['Content-Type'] = 'application/json'
  
  puts "📡 發送 API 請求..."
  response = http.request(request)
  
  puts "HTTP Status: #{response.code} #{response.message}"
  puts "X-Request-ID: #{response['x-request-id']}"
  puts ""
  
  if response.code == '200'
    puts "✅ API 呼叫成功！"
    data = JSON.parse(response.body)
    puts "找到 #{data['data']&.length || 0} 個 App"
  else
    puts "❌ API 呼叫失敗"
    puts "Response Body:"
    puts response.body
    
    # 解析錯誤詳細資訊
    begin
      error_body = JSON.parse(response.body)
      puts ""
      puts "===== 錯誤詳細資訊 ====="
      puts "X-Request-ID: #{response['x-request-id']}"
      if error_body['errors'] && error_body['errors'].first
        error = error_body['errors'].first
        puts "Error ID: #{error['id']}" if error['id']
        puts "Error Code: #{error['code']}" if error['code']
        puts "Error Detail: #{error['detail']}" if error['detail']
        puts "Error Status: #{error['status']}" if error['status']
        puts "Error Title: #{error['title']}" if error['title']
      end
    rescue JSON::ParserError
      puts "無法解析錯誤回應為 JSON"
    end
  end
  
rescue => e
  puts "❌ 執行錯誤: #{e.message}"
  puts e.backtrace.first(3)
end