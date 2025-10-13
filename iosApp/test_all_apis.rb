#!/usr/bin/env ruby

require 'jwt'
require 'net/http'
require 'json'
require 'time'

# 所有 API Key 設定
API_KEYS = [
  {
    name: "linli-news-key",
    key_id: "3SFDZ34ZZ6",
    issuer_id: "27e9b9fc-fa64-44cf-b04c-ccf4068cf35c",
    file_path: "/Users/linli/Downloads/AuthKey_3SFDZ34ZZ6.p8",
    role: "App 管理"
  },
  {
    name: "news_key", 
    key_id: "U29B7HP7M",
    issuer_id: "27e9b9fc-fa64-44cf-b04c-ccf4068cf35c",
    file_path: "/Users/linli/Downloads/AuthKey_U298B7HP7M.p8",
    role: "管理"
  },
  {
    name: "key2",
    key_id: "WMJC3PRN6V", 
    issuer_id: "27e9b9fc-fa64-44cf-b04c-ccf4068cf35c",
    file_path: "/Users/linli/Downloads/AuthKey_WMJC3PRN6V.p8",
    role: "App 管理"
  },
  {
    name: "個別金鑰",
    key_id: "ABGORDMI38HU",
    issuer_id: "27e9b9fc-fa64-44cf-b04c-ccf4068cf35c", 
    file_path: "/Users/linli/Downloads/AuthKey_ABGORDMI38HU.p8",
    role: "帳號持有人、管理"
  }
]

def test_api_key(api_config)
  puts "\n" + "="*60
  puts "🔑 測試 API Key: #{api_config[:name]}"
  puts "   Key ID: #{api_config[:key_id]}"
  puts "   Issuer ID: #{api_config[:issuer_id]}"
  puts "   角色: #{api_config[:role]}"
  puts "="*60
  
  begin
    # 檢查檔案是否存在
    unless File.exist?(api_config[:file_path])
      puts "❌ 檔案不存在: #{api_config[:file_path]}"
      return
    end
    
    # 讀取私鑰
    private_key = File.read(api_config[:file_path])
    
    # 產生 JWT
    payload = {
      iss: api_config[:issuer_id],
      exp: Time.now.to_i + 20 * 60,  # 20 分鐘後過期
      aud: 'appstoreconnect-v1'
    }
    
    header = {
      alg: 'ES256',
      kid: api_config[:key_id],
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
    puts "X-Request-ID: #{response['x-request-id']}" if response['x-request-id']
    
    if response.code == '200'
      puts "✅ API 呼叫成功！"
      data = JSON.parse(response.body)
      apps_count = data['data']&.length || 0
      puts "找到 #{apps_count} 個 App"
      
      if apps_count > 0
        puts "\n📱 App 列表:"
        data['data'].each_with_index do |app, index|
          attrs = app['attributes'] || {}
          puts "  #{index + 1}. #{attrs['name']} (#{attrs['bundleId']})"
        end
      end
    else
      puts "❌ API 呼叫失敗"
      
      # 解析錯誤詳細資訊
      begin
        error_body = JSON.parse(response.body)
        puts "\n🔍 錯誤詳細資訊:"
        puts "   X-Request-ID: #{response['x-request-id'] || 'N/A'}"
        
        if error_body['errors'] && error_body['errors'].first
          error = error_body['errors'].first
          puts "   Error ID: #{error['id'] || 'N/A'}"
          puts "   Error Code: #{error['code'] || 'N/A'}"
          puts "   Error Status: #{error['status'] || 'N/A'}"
          puts "   Error Title: #{error['title'] || 'N/A'}"
          puts "   Error Detail: #{error['detail'] || 'N/A'}" if error['detail']
        else
          puts "   Raw Response: #{response.body[0..200]}..."
        end
      rescue JSON::ParserError
        puts "   無法解析錯誤回應為 JSON"
        puts "   Raw Response: #{response.body[0..200]}..."
      end
    end
    
  rescue => e
    puts "❌ 執行錯誤: #{e.message}"
    puts "   #{e.backtrace.first}"
  end
end

# 測試所有 API Key
puts "🚀 開始測試所有 App Store Connect API Key..."
puts "測試時間: #{Time.now}"

API_KEYS.each do |api_config|
  test_api_key(api_config)
  sleep(1)  # 避免太快連續呼叫 API
end

puts "\n" + "="*60
puts "🏁 所有測試完成"
puts "="*60