package _v3r.project.prompt.domain.enumtype;

public enum PromptTemplate {
    어해도 {
        @Override
        public String build(String promptContent) {
            return "Generate an artwork in the 어해도 (Korean Minhwa) style. "
                    + "A traditional Korean Minhwa-style painting featuring seven freshwater fish of various sizes (carp, catfish, trout) arranged in a vertical, "
                    + "flat composition with no perspective. The fish are calmly floating in pale water among soft, ink-brushed aquatic plants. On the left side,"
                    + " a large natural rock is depicted with red flowers (resembling Chinese lantern flowers or bleeding hearts) blooming from its cracks."
                    + " The painting uses soft, muted traditional colors like grey, brown, and pale yellow for the fish, with a light grayish background. "
                    + "A splash of red from the flowers adds contrast. "
                    + "The brushwork is delicate, the lines are sharp without any ink bleeding, evoking a serene, still atmosphere where nature and life coexist harmoniously in stillness. "
                    + "The painting features vivid, saturated traditional " + promptContent;
        }
    },
    산수도 {
        @Override
        public String build(String promptContent) {
            return "Generate an artwork in the style of traditional Korean ink landscape painting (산수화, 山水畫). "
                    + "The scene should include: " + promptContent + ". "
                    + "Use ink wash techniques only, with soft gradients of black, gray, and a warm yellowed paper texture to simulate aged hanji.";

        }
    },
    탱화 {
        @Override
        public String build(String promptContent) {
            return  "A traditional Korean Buddhist painting (Shinjung Taenghwa) enshrined on the left and right walls of the temple's central hall. "
                    + "It features a vivid fusion of native folk deities and Buddhist guardian gods, showcasing a uniquely Korean character blended with elements of folk belief. "
                    + "The painting expands from 39 to 104 deities, reflecting a diversification of spiritual functions. "
                    + "There are four compositional types: "
                    + "1) Dominated by the Great Vajra Deity (Dae-yejeok Geumgangsin), occupying one-third of the canvas with Jeseokcheon to the left, Daebeomcheon to the right, and Dongjin Bosal below; surrounded by star lords, wrathful kings, and celestial maidens. "
                    + "2) Focused on Jeseokcheon, Daebeomcheon, and Dongjin Bosal, arranged in dual layers of heavenly and guardian figures. "
                    + "3) Centered around Jeseokcheon with all deities surrounding him, including both unarmed bodhisattvas and armored warrior deities. "
                    + "4) Centered on Dongjin Bosal with only guardian figures like the Eight Generals and Twelve Zodiac Generals. "
                    + "The painting reflects a three-tier cosmology of heaven, earth, and the underworld, and uses vivid, traditional Buddhist art style. "
                    + promptContent;
        }
    };
    public abstract String build(String promptContent);
}
