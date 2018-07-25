package com.mqfcu7.jiangmeilan.avatar;

import java.util.ArrayList;
import java.util.List;

public class AvatarSuiteGenerator {
    public AvatarSuiteGenerator() {

    }

    public AvatarSuite randomAvatarSuite() {
        AvatarSuite as = new AvatarSuite();
        as.title = "好看的女生头像高清 蜜语季节";
        as.images_url = new ArrayList<>();
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/c0b2577153393133!400x400_big.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/353354b630a9aa5e!400x400_big.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/efb755d6db4be33e!400x400_big.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/7eccd5fb0ed224d4!400x400_big.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/e6ec6c3fc7942303!400x400_big.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/c84bf85105cf900e!275x275_big.jpg");

        return as;
    }

    public List<AvatarSuite> getBatchAvatarSuites(int n) {
        List<AvatarSuite> result = new ArrayList<>();

        AvatarSuite as = new AvatarSuite();
        as.title = "卡通动漫女生头像插画 温存记忆";
        as.images_url = new ArrayList<>();
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/2ca563620c21cd33!400x400_big.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/691baf66cc47b4db!400x400_big.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/3ca7f4649c4e7f08!400x400_big.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/86802c5c01a4e3d5!400x400_big.jpg");
        result.add(as);

        as = new AvatarSuite();
        as.title = "溺毙:粉色系优质女头.你有爱而不得的人吗";
        as.images_url = new ArrayList<>();
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/44572bfa5505456fbf183cf36bd0dad7!400x400.jpeg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/63b37d4745944902938c92fb97991476!400x400.jpeg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/70340162893b43a8ab41e3e32d0e9ffb!400x400.jpeg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/85b690797c6f4ad4a2714c14e838c079!400x400.jpeg");
        result.add(as);

        as = new AvatarSuite();
        as.title = "【情侣头像】安静幸福";
        as.images_url = new ArrayList<>();
        as.images_url.add("https://img2.woyaogexing.com/2018/07/23/021d41fe83b0aec0!400x400_big.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/23/998603bbca7fab08!400x400_big.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/23/727a4455f8d88cc3!400x400_big.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/23/c7757e678a47dee2!400x400_big.jpg");
        result.add(as);

        return result;
    }
}
