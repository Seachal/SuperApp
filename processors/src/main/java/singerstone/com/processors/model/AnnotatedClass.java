package singerstone.com.processors.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import singerstone.com.processors.TypeUtil;

/**
 * Created by JokAr on 16/8/8.
 */
public class AnnotatedClass {

    private TypeElement mTypeElement;
    private ArrayList<BindViewField> mFields;
    private ArrayList<OnClickMethod> mMethods;
    private Elements mElementUtils;

    public AnnotatedClass(TypeElement typeElement, Elements elements) {
        mTypeElement = typeElement;
        mElementUtils = elements;
        mFields = new ArrayList<>();
        mMethods = new ArrayList<>();
    }

    public String getFullClassName() {
        return mTypeElement.getQualifiedName().toString();
    }

    public void addField(BindViewField field) {
        mFields.add(field);
    }

    public void addMethod(OnClickMethod method) {
        mMethods.add(method);
    }

    public JavaFile generateFile(Filer mFiler) {

        /**
         *   @Override
         *   public void inject(final MainFragment host, Object source, Provider provider) {
         *
         *   }
         */
        //generateMethod
        MethodSpec.Builder injectMethod = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.get(mTypeElement.asType()), "host", Modifier.FINAL)
                .addParameter(TypeName.OBJECT, "source")
                .addParameter(TypeUtil.PROVIDER, "provider");

        /**
         *  host.rv_tools = (SuperScrollRecyclerView)(provider.findView(source, 2131492978));
         *
         */
        for (BindViewField field : mFields) {
            // find views
            injectMethod.addStatement("host.$N = ($T)(provider.findView(source, $L))",
                    field.getFieldName(),
                    ClassName.get(field.getFieldType()),
                    field.getResId());
        }

        /**
         *
         * View.OnClickListener listener = new View.OnClickListener() {
         *       @Override
         *       public void onClick(View view) {
         *         host.clickSubTitle();
         *       }
         *     } ;
         *     provider.findView(source, 2131493035).setOnClickListener(listener);
         *
         *
         */

        for (OnClickMethod method : mMethods) {
            TypeSpec listener = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(TypeUtil.ANDROID_ON_CLICK_LISTENER)
                    .addMethod(MethodSpec.methodBuilder("onClick")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.VOID)
                            .addParameter(TypeUtil.ANDROID_VIEW, "view")
                            .addStatement("host.$N()", method.getMethodName())
                            .build())
                    .build();
            injectMethod.addStatement("View.OnClickListener listener = $L ", listener);
            for (int id : method.getResIds()) {
                // set listeners
                injectMethod.addStatement("provider.findView(source, $L).setOnClickListener(listener)", id);
            }
        }

        /**
         *
         * public class QQLiveTestFragment$$ViewInject implements Inject<QQLiveTestFragment> {
         * }
         */
        //generaClass
        TypeSpec injectClass = TypeSpec.classBuilder(mTypeElement.getSimpleName() + "$$ViewInject")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(TypeUtil.INJET, TypeName.get(mTypeElement.asType())))
                .addMethod(injectMethod.build())
                .build();

        String packgeName = mElementUtils.getPackageOf(mTypeElement).getQualifiedName().toString();
        System.out.println("generateFile  " + packgeName);
        return JavaFile.builder(packgeName, injectClass).build();
    }
}
