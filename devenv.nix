{
  pkgs,
  lib,
  config,
  inputs,
  ...
}: {
  packages = with pkgs; [
    postman
  ];

  languages.java = {
    enable = true;
    maven.enable = true;
  };
}
