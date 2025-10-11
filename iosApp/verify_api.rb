#!/usr/bin/env ruby
require 'spaceship'
require 'json'

key_id = ENV['ASC_KEY_ID']
issuer_id = ENV['ASC_ISSUER_ID']
key_path = ENV['ASC_PRIVATE_KEY_PATH']

unless key_id && issuer_id && key_path
  abort "請設 ASC_KEY_ID, ASC_ISSUER_ID, ASC_PRIVATE_KEY_PATH"
end

puts "Key ID: #{key_id}"
puts "Issuer ID: #{issuer_id}"
puts "Key Path: #{key_path}"
puts "Key file exists: #{File.exist?(key_path)}"
puts "\n"

# 讀取私鑰
key_content = File.read(key_path)

# 建立 token
token = Spaceship::ConnectAPI::Token.create(
  key_id: key_id,
  issuer_id: issuer_id,
  key: key_content
)

puts "生成的 Token (前50字元): #{token.text[0..50]}...\n\n"

# 設定 Spaceship 使用這個 token
Spaceship::ConnectAPI.token = token

# 呼叫 /v1/apps
begin
  puts "正在呼叫 Apple API /v1/apps..."
  client = Spaceship::ConnectAPI.client
  response = client.get("apps")
  puts "✅ Apps 回應成功！"
  puts "回應資料："
  puts JSON.pretty_generate(response)
rescue => e
  puts "❌ 錯誤發生："
  puts "錯誤類別: #{e.class}"
  puts "錯誤訊息: #{e.message}"
  if e.respond_to?(:preferred_error_info)
    puts "詳細錯誤："
    puts JSON.pretty_generate(e.preferred_error_info)
  end
end
