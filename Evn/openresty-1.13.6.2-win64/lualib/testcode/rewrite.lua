--
-- Tistargate rewrite_by_lua_file
ngx.log(ngx.DEBUG, 'In rewrite_by_lua_file...')

local start = os.clock()
local res = ngx.location.capture('/agbasdfds?domain=' .. ngx.var.http_host .. '&url=' .. ngx.var.real_referer ..
        '&token=' .. (ngx.var.cookie_TSG_Token or '') .. '&realIP=' .. ngx.var.remote_addr .. '&method=' .. ngx.var.request_method);
ngx.log(ngx.DEBUG, 'Lua sub request took ' .. string.format("%.2f ms", ((os.clock() - start) * 1000)))
if 302 == res.status then
    ngx.header.location = res.header['Location']
    ngx.exit(302)
end
if 401 == res.status then
    ngx.header.location = 'https://user.sdptest.tistarsec.com?redirect=' .. ngx.var.real_referer
    ngx.exit(307)
end
if 200 ~= res.status then
    ngx.log(ngx.WARN, ngx.var.http_host .. res.status)
    ngx.exit(res.status)
end


ngx.log(ngx.INFO, 'New target:' .. ngx.var.proxy_url .. 'Real host:' .. ngx.var.real_host .. 'Real referer:' .. ngx.var.real_referer ..
        'TSG token:' .. (ngx.var.tsg_token or 'null') .. 'Login success response header:' .. (ngx.var.login_success_res_header or 'null') ..
        'Cookie secret:' .. (ngx.var.cookie_secret or 'null'))

ngx.log(ngx.DEBUG, 'End rewrite_by_lua_file...')
