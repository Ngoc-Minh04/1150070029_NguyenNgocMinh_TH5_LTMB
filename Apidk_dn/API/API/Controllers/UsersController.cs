using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MyAPI.Data;
using MyAPI.Models;
using BCrypt.Net;

namespace MyAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsersController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        public UsersController(ApplicationDbContext context)
        {
            _context = context;
        }

        // GET: api/Users
        [HttpGet]
        public async Task<ActionResult<ApiResponse<List<UserResponseDto>>>> GetUsers()
        {
            try
            {
                var users = await _context.Users
                    .Select(u => new UserResponseDto
                    {
                        Id = u.Id,
                        Email = u.Email,
                        FullName = u.FullName,
                        CreatedAt = u.CreatedAt
                    })
                    .ToListAsync();

                return Ok(new ApiResponse<List<UserResponseDto>>
                {
                    Success = true,
                    Message = "Lấy danh sách user thành công",
                    Data = users
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ApiResponse<List<UserResponseDto>>
                {
                    Success = false,
                    Message = $"Lỗi: {ex.Message}",
                    Data = null
                });
            }
        }

        // GET: api/Users/5
        [HttpGet("{id}")]
        public async Task<ActionResult<ApiResponse<UserResponseDto>>> GetUser(int id)
        {
            try
            {
                var user = await _context.Users.FindAsync(id);

                if (user == null)
                {
                    return NotFound(new ApiResponse<UserResponseDto>
                    {
                        Success = false,
                        Message = "Không tìm thấy user",
                        Data = null
                    });
                }

                var userResponse = new UserResponseDto
                {
                    Id = user.Id,
                    Email = user.Email,
                    FullName = user.FullName,
                    CreatedAt = user.CreatedAt
                };

                return Ok(new ApiResponse<UserResponseDto>
                {
                    Success = true,
                    Message = "Lấy thông tin user thành công",
                    Data = userResponse
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ApiResponse<UserResponseDto>
                {
                    Success = false,
                    Message = $"Lỗi: {ex.Message}",
                    Data = null
                });
            }
        }

        // POST: api/Users/register
        [HttpPost("register")]
        public async Task<ActionResult<ApiResponse<UserResponseDto>>> Register(RegisterDto registerDto)
        {
            try
            {
                // Kiểm tra email đã tồn tại
                if (await _context.Users.AnyAsync(u => u.Email == registerDto.Email))
                {
                    return BadRequest(new ApiResponse<UserResponseDto>
                    {
                        Success = false,
                        Message = "Email đã tồn tại",
                        Data = null
                    });
                }

                // Hash password
                string hashedPassword = BCrypt.Net.BCrypt.HashPassword(registerDto.Password);

                // Tạo user mới
                var user = new User
                {
                    Email = registerDto.Email,
                    Password = hashedPassword,
                    FullName = registerDto.FullName,
                    CreatedAt = DateTime.Now
                };

                _context.Users.Add(user);
                await _context.SaveChangesAsync();

                var userResponse = new UserResponseDto
                {
                    Id = user.Id,
                    Email = user.Email,
                    FullName = user.FullName,
                    CreatedAt = user.CreatedAt
                };

                return CreatedAtAction(nameof(GetUser), new { id = user.Id },
                    new ApiResponse<UserResponseDto>
                    {
                        Success = true,
                        Message = "Đăng ký thành công",
                        Data = userResponse
                    });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ApiResponse<UserResponseDto>
                {
                    Success = false,
                    Message = $"Lỗi: {ex.Message}",
                    Data = null
                });
            }
        }

        // POST: api/Users/login
        [HttpPost("login")]
        public async Task<ActionResult<ApiResponse<UserResponseDto>>> Login(LoginDto loginDto)
        {
            try
            {
                // Tìm user theo email
                var user = await _context.Users
                    .FirstOrDefaultAsync(u => u.Email == loginDto.Email);

                if (user == null)
                {
                    return BadRequest(new ApiResponse<UserResponseDto>
                    {
                        Success = false,
                        Message = "Email hoặc mật khẩu không đúng",
                        Data = null
                    });
                }

                // Verify password
                if (!BCrypt.Net.BCrypt.Verify(loginDto.Password, user.Password))
                {
                    return BadRequest(new ApiResponse<UserResponseDto>
                    {
                        Success = false,
                        Message = "Email hoặc mật khẩu không đúng",
                        Data = null
                    });
                }

                var userResponse = new UserResponseDto
                {
                    Id = user.Id,
                    Email = user.Email,
                    FullName = user.FullName,
                    CreatedAt = user.CreatedAt
                };

                return Ok(new ApiResponse<UserResponseDto>
                {
                    Success = true,
                    Message = "Đăng nhập thành công",
                    Data = userResponse
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ApiResponse<UserResponseDto>
                {
                    Success = false,
                    Message = $"Lỗi: {ex.Message}",
                    Data = null
                });
            }
        }

        // PUT: api/Users/5
        [HttpPut("{id}")]
        public async Task<ActionResult<ApiResponse<UserResponseDto>>> UpdateUser(int id, RegisterDto updateDto)
        {
            try
            {
                var user = await _context.Users.FindAsync(id);

                if (user == null)
                {
                    return NotFound(new ApiResponse<UserResponseDto>
                    {
                        Success = false,
                        Message = "Không tìm thấy user",
                        Data = null
                    });
                }

                // Kiểm tra email mới có trùng với user khác không
                if (await _context.Users.AnyAsync(u => u.Email == updateDto.Email && u.Id != id))
                {
                    return BadRequest(new ApiResponse<UserResponseDto>
                    {
                        Success = false,
                        Message = "Email đã tồn tại",
                        Data = null
                    });
                }

                user.Email = updateDto.Email;
                user.FullName = updateDto.FullName;

                if (!string.IsNullOrEmpty(updateDto.Password))
                {
                    user.Password = BCrypt.Net.BCrypt.HashPassword(updateDto.Password);
                }

                await _context.SaveChangesAsync();

                var userResponse = new UserResponseDto
                {
                    Id = user.Id,
                    Email = user.Email,
                    FullName = user.FullName,
                    CreatedAt = user.CreatedAt
                };

                return Ok(new ApiResponse<UserResponseDto>
                {
                    Success = true,
                    Message = "Cập nhật thành công",
                    Data = userResponse
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ApiResponse<UserResponseDto>
                {
                    Success = false,
                    Message = $"Lỗi: {ex.Message}",
                    Data = null
                });
            }
        }

        // DELETE: api/Users/5
        [HttpDelete("{id}")]
        public async Task<ActionResult<ApiResponse<object>>> DeleteUser(int id)
        {
            try
            {
                var user = await _context.Users.FindAsync(id);

                if (user == null)
                {
                    return NotFound(new ApiResponse<object>
                    {
                        Success = false,
                        Message = "Không tìm thấy user",
                        Data = null
                    });
                }

                _context.Users.Remove(user);
                await _context.SaveChangesAsync();

                return Ok(new ApiResponse<object>
                {
                    Success = true,
                    Message = "Xóa user thành công",
                    Data = null
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ApiResponse<object>
                {
                    Success = false,
                    Message = $"Lỗi: {ex.Message}",
                    Data = null
                });
            }
        }
    }
}