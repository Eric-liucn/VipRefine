## 功能需求
* [x] 添加用户到vip组
* [x] 查看用户当前vip组的到期时间
* [x] 延长或者缩短用户vip组到期时间
* [x] 管理用户的进服欢迎信息
* [x] 设置vip礼包的绑定指令
* [x] 设置vip礼包的绑定给予经验
* [x] 管理vip组的每日礼包
* [x] 管理vip组的每日礼包的指令
* [ ] 发卡系统

## 指令
* [x] /viprefine vip [player] [group] [Days] 将一个用户设置为指定vip组指定时间
* [x] /viprefine playerinfo [player] 查看该用户的VIP信息
* [x] /viprefine groupinfo [group] 查看该vip组的信息
* [ ] /viprefine modifytime [player] [时间] 修改玩家在当前vip组的时间
* [x] /viprefine dailykit 领取每日礼包

## 配置文件结构
    Groups{
        VIP{
            kits{
                kit1{
                    kitName="vip"
                    kitCommands{
                        "give %player% minecraft:wood 1"
                    }
                    kitExp=10
                }
                kit2{
                    kitName="vip1"
                    kitCommands{
                        "warp %player% dp"
                    }
                    kitExp=10
                }
            }
            DailyKit{
                dailyKit1{
                    kitName="vipDaily1"
                    kitCommands{
                        "give %player% minecraft:wowl 1"
                    }
                    kitExp=10
                }
                dailyKit2{
                    kitName="vipDaily2"
                    kitCommands{
                        ""
                    }
                    kitExp=10
                }
            }
            JoinBroadCast="&l&a欢迎 VIP 玩家 %player% 加入游戏"
        }
        
        SVIP{
        }
        
        MVIP{
        }
    }