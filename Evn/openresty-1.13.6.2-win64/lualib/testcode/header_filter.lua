--
-- Tistargate header_filter_by_lua_file
ngx.log(ngx.DEBUG, 'In header_filter_by_lua_file...')

local header, err = ngx.resp.get_headers()
    if header["sdp-auth"] then
        ngx.log(ngx.INFO, '132456789, et TSG_Token to cookie successfully')
    else
        ngx.log(ngx.INFO, 'Login processing uri matched, but login_success_res_header is not matched:' .. ngx.var.login_success_res_header)
    end



