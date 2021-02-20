--
-- Tistargate log_by_lua_file
ngx.log(ngx.DEBUG, 'In log_by_lua_file...')
--judge the response is download type request or not
--download type request, should post a request to create a download log
local header, err = ngx.resp.get_headers()
local download_flag = false
local filename = nil
local file_size = nil
local status = 'success'
if 200 ~= ngx.status then --Note: this type is reliable??
    status = 'fail'
end
if header['Content-Disposition'] then
    download_flag = true
    file_size = header['Content-Length']
    local value = header['Content-Disposition']
    ngx.log(ngx.DEBUG, 'Content-Disposition:' .. value)
    local reverse_value = string.reverse(value)
    ngx.log(ngx.DEBUG, 'reverse_value:' .. reverse_value)
    local _, i = string.find(reverse_value, '=')
    ngx.log(ngx.DEBUG, 'Found = index:' .. (i or 'none'))
    if i then
        local len = string.len(value)
        ngx.log(ngx.DEBUG, "length" .. len)
        local encoded_filename = string.sub(value, len - i + 2, len)

        encoded_filename = string.gsub(encoded_filename, "\"", "")
        encoded_filename = string.gsub(encoded_filename, ";", "")

        ngx.log(ngx.DEBUG, 'Encoded_filename:' .. encoded_filename)

        filename = string.gsub(encoded_filename, '%%(%x%x)', function(h) return string.char(tonumber(h, 16)) end)
        ngx.log(ngx.DEBUG, 'Filename:' .. filename)
    end
end

ngx.log(ngx.DEBUG, 'End log_by_lua_file...')

