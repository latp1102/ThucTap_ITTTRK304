$connection = New-Object MySqlConnection
$connection.ConnectionString = "Server=localhost;Port=3306;Database=bton_db;User=root;Password=1234"
$connection.Open()

$command = $connection.CreateCommand()
$command.CommandText = @"
UPDATE bton_db.users SET password_hash = '$2a$10$JizcpfkO7BoN5tAcZJQHB.6mnaSYOgEveja8KLEYbOuj9Lx7uKtvG' WHERE username = 'admin';
UPDATE bton_db.users SET email = 'admin@bton.app' WHERE username = 'admin';
"@

$command.ExecuteNonQuery()
$connection.Close()
Write-Host "Database updated successfully"