import React from "react";
import { useAuth } from "../contexts/AuthContext";
import { Avatar, Button } from "@mui/material";

const Header: React.FC = () => {
  const { isLoggedIn, user, logout } = useAuth();

  return (
    <header className="flex justify-between items-center p-4 bg-white shadow-sm">
      <div className="text-xl font-bold">로고</div>
      <div>
        {isLoggedIn ? (
          <div className="flex items-center gap-4">
            <Avatar
              src={user.profileImage}
              alt={user.name}
              className="cursor-pointer"
            />
            <Button variant="outlined" onClick={logout}>
              로그아웃
            </Button>
          </div>
        ) : (
          <Button variant="contained" color="primary" href="/login">
            로그인
          </Button>
        )}
      </div>
    </header>
  );
};

export default Header;
